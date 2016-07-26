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
	@Value("${hub.server.url:}")
	private String hubServerUrl;

	@Value("${smtp.host:}")
	private String smtpHost;

	@Value("${smtp.port:}")
	private String smtpPort;

	@Value("${smtp.auth:}")
	private String smtpAuth;

	@Value("${smtp.username:}")
	private String smtpUsername;

	@Value("${smtp.password:}")
	private String smtpPassword;

	@Value("${email.from.address:}")
	private String emailFromAddress;

	@Value("${email.reply.to.address:}")
	private String emailReplyToAddress;

	@Value("${template.name:}")
	private String templateName;

	@Value("${property.file:}")
	private String propertyFilePath;

	public String getHubServerUrl() {
		return hubServerUrl;
	}

	public void setHubServerUrl(final String hubServerUrl) {
		this.hubServerUrl = hubServerUrl;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(final String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(final String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(final String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(final String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(final String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getEmailFromAddress() {
		return emailFromAddress;
	}

	public void setEmailFromAddress(final String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}

	public String getEmailReplyToAddress() {
		return emailReplyToAddress;
	}

	public void setEmailReplyToAddress(final String emailReplyToAddress) {
		this.emailReplyToAddress = emailReplyToAddress;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	public String getPropertyFilePath() {
		return propertyFilePath;
	}

	public void setPropertyFilePath(final String propertyFilePath) {
		this.propertyFilePath = propertyFilePath;
	}

}
