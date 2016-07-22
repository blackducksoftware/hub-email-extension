package com.blackducksoftware.integration.email.model;

public class EmailUserConfiguration {
	private boolean optIn;
	private String templateName;

	public boolean isOptIn() {
		return optIn;
	}

	public void setOptIn(final boolean optIn) {
		this.optIn = optIn;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

}
