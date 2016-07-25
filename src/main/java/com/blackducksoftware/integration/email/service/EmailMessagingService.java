package com.blackducksoftware.integration.email.service;

import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.email.model.SmtpProperties;

@Service
public class EmailMessagingService {
	public static void main(final String[] args) {
		final Properties props = new Properties();
		props.put("mail.smtp.host", "mailrelay.blackducksoftware.com");
		props.put("mail.smtp.port", Integer.toString(25));
		props.put("mail.smtp.auth", "true");

		final Session session = Session.getInstance(props);

		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("giggles@blackducksoftware.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("eric.kerwin@gmail.com"));
			message.setSubject("Testing Hub Email Extension");
			message.setText("We are as Midas.");

			Transport.send(message);
		} catch (final MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendEmailMessage(final EmailSystemConfiguration emailSystemConfiguration) {
		final SmtpProperties smtpProperties = emailSystemConfiguration.getSmtpProperties();

		final Map<String, String> sessionProps = smtpProperties.getPropertiesForSession();
		final Properties props = System.getProperties();
		props.putAll(sessionProps);

		final Session session = Session.getInstance(props);

		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("giggles@blackducksoftware.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ekerwin@blackducksoftware.com"));
			message.setSubject("Testing Hub Email Extension");
			message.setText("We are as Midas.");

			if (smtpProperties.isAuth()) {
				sendAuthenticated(message, session, smtpProperties);
			} else {
				Transport.send(message);
			}
		} catch (final MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendAuthenticated(final Message message, final Session session, final SmtpProperties smtpProperties)
			throws MessagingException {
		final String host = smtpProperties.getHost();
		final int port = smtpProperties.getPort();
		final String username = smtpProperties.getUsername();
		final String password = smtpProperties.getPassword();

		final Transport transport = session.getTransport("smtp");
		try {
			transport.connect(host, port, username, password);
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
		} finally {
			transport.close();
		}
	}

}
