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
	@Value("${smtp.host:''}")
	private String smtpHost;

	@Value("${smtp.port:''}")
	private String smtpPort;

	@Value("${smtp.auth:''}")
	private String smtpAuth;

	@Value("${smtp.username:''}")
	private String smtpUsername;

	@Value("${smtp.password:''}")
	private String smtpPassword;

	@Value("${email.from.address:''}")
	private String emailFromAddress;

	@Value("${email.reply.to.address:''}")
	private String emailReplyToAddress;

	@Value("${template.name:''}")
	private String templateName;

	@Value("${property.file:''}")
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
