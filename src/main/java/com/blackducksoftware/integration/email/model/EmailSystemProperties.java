package com.blackducksoftware.integration.email.model;

import java.util.Properties;

/**
 * Encapsulates the current properties from the file system, json, the hub, etc.
 * Only java.lang.String's and java.util.List<java.lang.String>'s should be used
 * here.
 */
public class EmailSystemProperties {
	public final static String KEY_EMAIL_FROM_ADDRESS = "email.from.address";
	public final static String KEY_EMAIL_REPLY_TO_ADDRESS = "email.reply.to.address";
	public final static String KEY_HUB_SERVER_URL = "hub.server.url";
	public final static String KEY_HUB_SERVER_USER = "hub.server.user";
	public final static String KEY_HUB_SERVER_PASSWORD = "hub.server.password";
	public final static String KEY_HUB_SERVER_TIMEOUT = "hub.server.timeout";
	public final static String KEY_HUB_PROXY_HOST = "hub.proxy.host";
	public final static String KEY_HUB_PROXY_PORT = "hub.proxy.port";
	public final static String KEY_HUB_PROXY_USER = "hub.proxy.user";
	public final static String KEY_HUB_PROXY_PASSWORD = "hub.proxy.password";
	public final static String KEY_HUB_PROXY_NOHOST = "hub.proxy.nohost";
	public final static String KEY_EMAIL_SERVICE_DISPATCHER_NOTIFICATION_INTERVAL = "email.service.dispatcher.notification.interval";
	public final static String KEY_EMAIL_SERVICE_DISPATCHER_NOTIFICATION_DELAY = "email.service.dispatcher.notification.delay";

	private final Properties properties;

	public EmailSystemProperties(final Properties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("properties argument cannot be null");
		}
		this.properties = properties;
	}

	public String getEmailFromAddress() {
		return properties.getProperty(KEY_EMAIL_FROM_ADDRESS);
	}

	public String getEmailReplyToAddress() {
		return properties.getProperty(KEY_EMAIL_REPLY_TO_ADDRESS);
	}

	public String getHubServerUrl() {
		return properties.getProperty(KEY_HUB_SERVER_URL);
	}

	public String getHubServerUser() {
		return properties.getProperty(KEY_HUB_SERVER_USER);
	}

	public String getHubServerPassword() {
		return properties.getProperty(KEY_HUB_SERVER_PASSWORD);
	}

	public String getHubServerTimeout() {
		return properties.getProperty(KEY_HUB_SERVER_TIMEOUT);
	}

	public String getHubProxyHost() {
		return properties.getProperty(KEY_HUB_PROXY_HOST);
	}

	public String getHubProxyPort() {
		return properties.getProperty(KEY_HUB_PROXY_PORT);
	}

	public String getHubProxyUser() {
		return properties.getProperty(KEY_HUB_PROXY_USER);
	}

	public String getHubProxyPassword() {
		return properties.getProperty(KEY_HUB_PROXY_PASSWORD);
	}

	public String getHubProxyNoHost() {
		return properties.getProperty(KEY_HUB_PROXY_NOHOST);
	}

	public String getNotificationInterval() {
		return properties.getProperty(KEY_EMAIL_SERVICE_DISPATCHER_NOTIFICATION_INTERVAL);
	}

	public String getNotificationStartupDelay() {
		return properties.getProperty(KEY_EMAIL_SERVICE_DISPATCHER_NOTIFICATION_DELAY);
	}
}
