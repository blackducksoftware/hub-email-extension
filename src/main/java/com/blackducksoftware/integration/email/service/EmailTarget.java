package com.blackducksoftware.integration.email.service;

import java.util.Map;

public class EmailTarget {
	private final String emailAddress;
	private final String templateName;
	private final Map<String, Object> model;

	public EmailTarget(final String emailAddress, final String templateName, final Map<String, Object> model) {
		this.emailAddress = emailAddress;
		this.templateName = templateName;
		this.model = model;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getTemplateName() {
		return templateName;
	}

	public Map<String, Object> getModel() {
		return model;
	}

}
