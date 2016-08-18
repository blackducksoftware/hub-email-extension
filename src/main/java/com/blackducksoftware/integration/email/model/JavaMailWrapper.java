package com.blackducksoftware.integration.email.model;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public class JavaMailWrapper {
	public void sendMessage(final CustomerProperties customerProperties, final Session session, final Message message)
			throws MessagingException {
		if (customerProperties.isAuth()) {
			sendAuthenticated(customerProperties, message, session);
		} else {
			Transport.send(message);
		}
	}

	private void sendAuthenticated(final CustomerProperties customerProperties, final Message message,
			final Session session) throws MessagingException {
		final String host = customerProperties.getHost();
		final int port = customerProperties.getPort();
		final String username = customerProperties.getUsername();
		final String password = customerProperties.getPassword();

		final Transport transport = session.getTransport("smtp");
		try {
			transport.connect(host, port, username, password);
			transport.sendMessage(message, message.getAllRecipients());
		} finally {
			transport.close();
		}
	}

}
