package com.blackducksoftware.integration.email.service;

import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.email.model.SmtpConfiguration;

@Service
public class EmailMessagingService {

	@Autowired
	private SmtpConfiguration smtpConfiguration;

	public static void test(final String[] args) {
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
		final Map<String, String> sessionProps = smtpConfiguration.getPropertiesForSession();
		final Properties props = System.getProperties();
		props.putAll(sessionProps);

		final Session session = Session.getInstance(props);

		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("giggles@blackducksoftware.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ekerwin@blackducksoftware.com"));
			message.setSubject("Testing Hub Email Extension");
			message.setText("We are as Midas.");

			if (smtpConfiguration.isAuth()) {
				sendAuthenticated(message, session, smtpConfiguration);
			} else {
				Transport.send(message);
			}
		} catch (final MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendAuthenticated(final Message message, final Session session,
			final SmtpConfiguration smtpConfiguration) throws MessagingException {
		final String host = smtpConfiguration.getHost();
		final int port = smtpConfiguration.getPort();
		final String username = smtpConfiguration.getUsername();
		final String password = smtpConfiguration.getPassword();

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
