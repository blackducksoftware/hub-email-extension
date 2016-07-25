package com.blackducksoftware.integration.email.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Encapsulates the current properties from the file system, json, the hub, etc.
 * Only java.lang.String's and java.util.List<java.lang.String>'s should be used
 * here.
 */
@Component
public class EmailSystemProperties {
	@Value("${smtp.host}")
	private String smtpHost;

	@Value("${smtp.host}")
	private String smtpPort;

	@Value("${smtp.host}")
	private String smtpAuth;

	@Value("${smtp.host}")
	private String smtpUsername;

	@Value("${smtp.host}")
	private String smtpPassword;

	@Value("${smtp.host}")
	private String emailFromAddress;

	@Value("${smtp.host}")
	private String emailReplyToAddress;

	@Value("${smtp.host}")
	private String templateName;

	@Value("${propertyFile}")
	private String propertyFilePath;

	public String getSmtpHost() {
		return smtpHost;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public String getSmtpAuth() {
		return smtpAuth;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public String getEmailFromAddress() {
		return emailFromAddress;
	}

	public String getEmailReplyToAddress() {
		return emailReplyToAddress;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getPropertyFilePath() {
		return propertyFilePath;
	}

}
