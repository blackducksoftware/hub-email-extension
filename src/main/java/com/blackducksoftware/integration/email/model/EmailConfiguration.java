package com.blackducksoftware.integration.email.model;

import java.util.List;

public class EmailConfiguration {
	private boolean optIn;
	private String templateName;
	private EmailFrequencyEnum emailFrequency;
	private List<EmailTriggerEnum> emailTriggers;
	private List<String> emailTriggeringProjects;
	private List<String> emailTriggeringPolicies;

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

	public EmailFrequencyEnum getEmailFrequency() {
		return emailFrequency;
	}

	public void setEmailFrequency(final EmailFrequencyEnum emailFrequency) {
		this.emailFrequency = emailFrequency;
	}

	public List<EmailTriggerEnum> getEmailTriggers() {
		return emailTriggers;
	}

	public void setEmailTriggers(final List<EmailTriggerEnum> emailTriggers) {
		this.emailTriggers = emailTriggers;
	}

	public List<String> getEmailTriggeringProjects() {
		return emailTriggeringProjects;
	}

	public void setEmailTriggeringProjects(final List<String> emailTriggeringProjects) {
		this.emailTriggeringProjects = emailTriggeringProjects;
	}

	public List<String> getEmailTriggeringPolicies() {
		return emailTriggeringPolicies;
	}

	public void setEmailTriggeringPolicies(final List<String> emailTriggeringPolicies) {
		this.emailTriggeringPolicies = emailTriggeringPolicies;
	}
}
