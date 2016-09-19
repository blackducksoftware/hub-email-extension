package com.blackducksoftware.integration.email.model;

import java.io.File;
import java.io.FileOutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

// this mail wrapper is for testing purposes and can be swapped in the EmailEngine with the base class.
public class FileMailWrapper extends JavaMailWrapper {

	private int index = 0;

	@Override
	public void sendMessage(final CustomerProperties customerProperties, final Session session, final Message message)
			throws MessagingException {
		final File parent = new File(customerProperties.getEmailTemplateDirectory());
		final File file = new File(parent.getParentFile(), createFileName());

		try (FileOutputStream fileOutput = new FileOutputStream(file)) {
			file.createNewFile();
			message.writeTo(fileOutput);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	private String createFileName() {
		return "Test_Message_" + index++;
	}
}
