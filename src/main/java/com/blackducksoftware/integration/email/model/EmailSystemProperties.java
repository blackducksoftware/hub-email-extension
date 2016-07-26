package com.blackducksoftware.integration.email.model;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.service.properties.ServicePropertiesBuilder;

/**
 * Encapsulates the current properties from the file system, json, the hub, etc.
 * Only java.lang.String's and java.util.List<java.lang.String>'s should be used
 * here.
 */
@Component
public class EmailSystemProperties {
	private static Logger logger = LoggerFactory.getLogger(EmailSystemProperties.class);

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

	@Value("${hub.server.url:}")
	private String hubServerUrl;

	@Value("${hub.server.user:}")
	private String hubServerUser;

	@Value("${hub.server.password:}")
	private String hubServerPassword;

	@Value("${hub.server.timeout:}")
	private String hubServerTimeout;

	@Value("${hub.proxy.host:}")
	private String hubProxyHost;

	@Value("${hub.proxy.port:}")
	private String hubProxyPort;

	@Value("${hub.proxy.user:}")
	private String hubProxyUser;

	@Value("${hub.proxy.password:}")
	private String hubProxyPassword;

	@Value("${hub.proxy.nohost:}")
	private String hubProxyNoHost;

	@PostConstruct
	private void checkPropertiesFile() {
		try {
			final ServicePropertiesBuilder propBuilder = new ServicePropertiesBuilder();
			propBuilder.setFilePath(getPropertyFilePath());

			if (!propBuilder.propertyFileExists()) {
				logger.error("Properties file for the service does not exist prior to service startup.");
				logger.error("A properties file for the service has been generated at the following location: "
						+ propBuilder.getFilePath());
			}
		} catch (final IOException e) {
			logger.error("Error occurred checking for the properties file", e);
		}
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

	public String getHubServerUrl() {
		return hubServerUrl;
	}

	public void setHubServerUrl(final String hubServerUrl) {
		this.hubServerUrl = hubServerUrl;
	}

	public String getHubServerUser() {
		return hubServerUser;
	}

	public void setHubServerUser(final String hubServerUser) {
		this.hubServerUser = hubServerUser;
	}

	public String getHubServerPassword() {
		return hubServerPassword;
	}

	public void setHubServerPassword(final String hubServerPassword) {
		this.hubServerPassword = hubServerPassword;
	}

	public String getHubServerTimeout() {
		return hubServerTimeout;
	}

	public void setHubServerTimeout(final String hubServerTimeout) {
		this.hubServerTimeout = hubServerTimeout;
	}

	public String getHubProxyHost() {
		return hubProxyHost;
	}

	public void setHubProxyHost(final String hubProxyHost) {
		this.hubProxyHost = hubProxyHost;
	}

	public String getHubProxyPort() {
		return hubProxyPort;
	}

	public void setHubProxyPort(final String hubProxyPort) {
		this.hubProxyPort = hubProxyPort;
	}

	public String getHubProxyUser() {
		return hubProxyUser;
	}

	public void setHubProxyUser(final String hubProxyUser) {
		this.hubProxyUser = hubProxyUser;
	}

	public String getHubProxyPassword() {
		return hubProxyPassword;
	}

	public void setHubProxyPassword(final String hubProxyPassword) {
		this.hubProxyPassword = hubProxyPassword;
	}

	public String getHubProxyNoHost() {
		return hubProxyNoHost;
	}

	public void setHubProxyNoHost(final String hubProxyNoHost) {
		this.hubProxyNoHost = hubProxyNoHost;
	}

}
