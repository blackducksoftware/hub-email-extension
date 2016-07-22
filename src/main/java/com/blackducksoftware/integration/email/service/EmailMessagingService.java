package com.blackducksoftware.integration.email.service;

import java.net.PasswordAuthentication;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class EmailMessagingService {
	public void sendEmailMessage() {
		final String username = "username@gmail.com";
		final String password = "password";

		final Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		final Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

}
