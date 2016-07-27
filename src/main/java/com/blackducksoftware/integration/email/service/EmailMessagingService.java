package com.blackducksoftware.integration.email.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.email.model.SmtpConfiguration;

@Service
public class EmailMessagingService {
	private final Logger log = LoggerFactory.getLogger(EmailMessagingService.class);

	@Autowired
	private EmailSystemProperties emailSystemProperties;

	@Autowired
	private SmtpConfiguration smtpConfiguration;

	public void sendEmailMessage(final List<String> emailAddresses, final String html) throws MessagingException {
		final Session session = createMailSession();
		final Message message = createMailMessage(session, html, emailAddresses);
		sendMessage(session, message);
	}

	private Session createMailSession() {
		final Map<String, String> sessionProps = smtpConfiguration.getPropertiesForSession();
		final Properties props = new Properties();
		props.putAll(sessionProps);

		return Session.getInstance(props);
	}

	private Message createMailMessage(final Session session, final String html,
			final List<String> recipientEmailAddresses) throws MessagingException {
		final List<InternetAddress> addresses = new ArrayList<>();
		for (final String recipient : recipientEmailAddresses) {
			try {
				addresses.add(new InternetAddress(recipient));
			} catch (final AddressException e) {
				log.warn(String.format("Could not create the address from %s: %s", recipient, e.getMessage()));
			}
		}

		if (addresses.isEmpty()) {
			throw new RuntimeException("There were no valid email addresses supplied.");
		}

		final Message message = new MimeMessage(session);
		final Multipart multiPart = new MimeMultipart("alternative");

		final String text = Jsoup.parse(html).text();

		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(text, "utf-8");

		final MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(html, "text/html; charset=utf-8");

		multiPart.addBodyPart(htmlPart);
		multiPart.addBodyPart(textPart);
		message.setContent(multiPart);

		message.setFrom(new InternetAddress(emailSystemProperties.getEmailFromAddress()));
		message.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[addresses.size()]));
		message.setSubject("Testing Hub Email Extension");

		return message;
	}

	private void sendMessage(final Session session, final Message message) throws MessagingException {
		if (smtpConfiguration.isAuth()) {
			sendAuthenticated(message, session);
		} else {
			Transport.send(message);
		}
	}

	private void sendAuthenticated(final Message message, final Session session) throws MessagingException {
		final String host = smtpConfiguration.getHost();
		final int port = smtpConfiguration.getPort();
		final String username = smtpConfiguration.getUsername();
		final String password = smtpConfiguration.getPassword();

		final Transport transport = session.getTransport("smtp");
		try {
			transport.connect(host, port, username, password);
			transport.sendMessage(message, message.getAllRecipients());
		} finally {
			transport.close();
		}
	}

}
