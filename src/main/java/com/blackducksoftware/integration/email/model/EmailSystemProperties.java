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
	@Value("${email.from.address:}")
	private String emailFromAddress;

	@Value("${email.reply.to.address:}")
	private String emailReplyToAddress;

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

	@Value("email.service.dispatcher.notification.interval:")
	private String notificationInterval;

	@Value("email.service.dispatcher.notification.delay:")
	private String notificationStartupDelay;

	@Value("email.service.dispatcher.configuration.interval:")
	private String configurationInterval;

	@Value("email.service.dispatcher.configuration.delay:")
	private String configurationStartupDelay;

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

	public String getNotificationInterval() {
		return notificationInterval;
	}

	public void setNotificationInterval(final String notificationInterval) {
		this.notificationInterval = notificationInterval;
	}

	public String getNotificationStartupDelay() {
		return notificationStartupDelay;
	}

	public void setNotificationStartupDelay(final String notificationStartupDelay) {
		this.notificationStartupDelay = notificationStartupDelay;
	}

	public String getConfigurationInterval() {
		return configurationInterval;
	}

	public void setConfigurationInterval(final String configurationInterval) {
		this.configurationInterval = configurationInterval;
	}

	public String getConfigurationStartupDelay() {
		return configurationStartupDelay;
	}

	public void setConfigurationStartupDelay(final String configurationStartupDelay) {
		this.configurationStartupDelay = configurationStartupDelay;
	}

}
