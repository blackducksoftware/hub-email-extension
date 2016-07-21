package com.blackducksoftware.integration.email.model;

import java.io.File;
import java.util.List;

public class EmailMessage {

	// this class is only for testing purposes only it will be replaced with a
	// correct implementation for email.
	private final String to;
	private final String from;
	private final String body;
	private final List<File> attachments;

	public EmailMessage(final String to, final String from, final String body, final List<File> attachments) {
		this.to = to;
		this.from = from;
		this.body = body;
		this.attachments = attachments;
	}

	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public String getBody() {
		return body;
	}

	public List<File> getAttachments() {
		return attachments;
	}

}
