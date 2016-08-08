package com.blackducksoftware.integration.email.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class CustomerProperties {
	// property keys
	public final static String EMAIL_FROM_ADDRESS_KEY = "email.from.address";
	public final static String EMAIL_REPLY_TO_ADDRESS_KEY = "email.reply.to.address";
	public final static String HUB_SERVER_URL_KEY = "hub.server.url";
	public final static String HUB_SERVER_USER_KEY = "hub.server.user";
	public final static String HUB_SERVER_PASSWORD_KEY = "hub.server.password";
	public final static String HUB_SERVER_TIMEOUT_KEY = "hub.server.timeout";
	public final static String HUB_PROXY_HOST_KEY = "hub.proxy.host";
	public final static String HUB_PROXY_PORT_KEY = "hub.proxy.port";
	public final static String HUB_PROXY_USER_KEY = "hub.proxy.user";
	public final static String HUB_PROXY_PASSWORD_KEY = "hub.proxy.password";
	public final static String HUB_PROXY_NOHOST_KEY = "hub.proxy.nohost";
	public final static String EMAIL_SERVICE_DISPATCHER_NOTIFICATION_INTERVAL_KEY = "email.service.dispatcher.notification.interval";
	public final static String EMAIL_SERVICE_DISPATCHER_NOTIFICATION_DELAY_KEY = "email.service.dispatcher.notification.delay";
	public final static String EMAIL_TEMPLATE_DIRECTORY = "hub.email.template.directory";

	// common javamail properties
	public static final String JAVAMAIL_HOST_KEY = "mail.smtp.host";
	public static final String JAVAMAIL_PORT_KEY = "mail.smtp.port";
	public static final String JAVAMAIL_AUTH_KEY = "mail.smtp.auth";
	public static final String JAVAMAIL_USER_KEY = "mail.smtp.user";

	// not a javamail property, but we are going to piggy-back on the
	// auto-parsing for javamail properties to get the password
	public static final String JAVAMAIL_PASSWORD_KEY = "mail.smtp.password";

	public static final String JAVAMAIL_CONFIG_PREFIX = "hub.email.javamail.config.";
	public static final String TEMPLATE_VARIABLE_PREFIX = "hub.email.template.variable.";
	public static final String ROUTER_FACTORY_PREFIX = "email.service.router.factory.";

	private final List<String> javamailConfigKeys = new ArrayList<>();
	private final Map<String, String> suppliedJavamailConfigProperties = new HashMap<>();

	private final List<String> templateVariableKeys = new ArrayList<>();
	private final Map<String, String> suppliedTemplateVariableProperties = new HashMap<>();
	private final Properties appProperties;
	private final List<String> factoryClassNames = new ArrayList<>();

	public CustomerProperties(final Properties appProperties) {
		if (appProperties == null) {
			throw new IllegalArgumentException("properties argument cannot be null");
		}
		this.appProperties = appProperties;
		extractProperties(appProperties);
	}

	public void extractProperties(final Properties properties) {
		for (final Object obj : properties.keySet()) {
			if (obj instanceof String) {
				final String key = (String) obj;
				final String value = properties.getProperty(key);
				if (StringUtils.isNotBlank(value)) {
					if (key.startsWith(JAVAMAIL_CONFIG_PREFIX)) {
						javamailConfigKeys.add(key);
						final String cleanedKey = key.replace(JAVAMAIL_CONFIG_PREFIX, "");
						suppliedJavamailConfigProperties.put(cleanedKey, value);
					} else if (key.startsWith(TEMPLATE_VARIABLE_PREFIX)) {
						templateVariableKeys.add(key);
						final String cleanedKey = key.replace(TEMPLATE_VARIABLE_PREFIX, "");
						suppliedTemplateVariableProperties.put(cleanedKey, value);
					} else if (key.startsWith(ROUTER_FACTORY_PREFIX)) {
						factoryClassNames.add(value);
					}
				}
			}
		}
	}

	public Map<String, String> getSuppliedJavamailConfigProperties() {
		return suppliedJavamailConfigProperties;
	}

	public Map<String, String> getSuppliedTemplateVariableProperties() {
		return suppliedTemplateVariableProperties;
	}

	public Map<String, String> getPropertiesForSession() {
		return getSuppliedJavamailConfigProperties();
	}

	public String getHost() {
		return getSuppliedJavamailConfigProperties().get(JAVAMAIL_HOST_KEY);
	}

	public int getPort() {
		return NumberUtils.toInt(getSuppliedJavamailConfigProperties().get(JAVAMAIL_PORT_KEY));
	}

	public boolean isAuth() {
		return Boolean.parseBoolean(getSuppliedJavamailConfigProperties().get(JAVAMAIL_AUTH_KEY));
	}

	public String getUsername() {
		return getSuppliedJavamailConfigProperties().get(JAVAMAIL_USER_KEY);
	}

	public String getPassword() {
		return getSuppliedJavamailConfigProperties().get(JAVAMAIL_PASSWORD_KEY);
	}

	public String getEmailFromAddress() {
		return appProperties.getProperty(EMAIL_FROM_ADDRESS_KEY);
	}

	public String getEmailReplyToAddress() {
		return appProperties.getProperty(EMAIL_REPLY_TO_ADDRESS_KEY);
	}

	public String getHubServerUrl() {
		return appProperties.getProperty(HUB_SERVER_URL_KEY);
	}

	public String getHubServerUser() {
		return appProperties.getProperty(HUB_SERVER_USER_KEY);
	}

	public String getHubServerPassword() {
		return appProperties.getProperty(HUB_SERVER_PASSWORD_KEY);
	}

	public String getHubServerTimeout() {
		return appProperties.getProperty(HUB_SERVER_TIMEOUT_KEY);
	}

	public String getHubProxyHost() {
		return appProperties.getProperty(HUB_PROXY_HOST_KEY);
	}

	public String getHubProxyPort() {
		return appProperties.getProperty(HUB_PROXY_PORT_KEY);
	}

	public String getHubProxyUser() {
		return appProperties.getProperty(HUB_PROXY_USER_KEY);
	}

	public String getHubProxyPassword() {
		return appProperties.getProperty(HUB_PROXY_PASSWORD_KEY);
	}

	public String getHubProxyNoHost() {
		return appProperties.getProperty(HUB_PROXY_NOHOST_KEY);
	}

	public String getNotificationInterval() {
		return appProperties.getProperty(EMAIL_SERVICE_DISPATCHER_NOTIFICATION_INTERVAL_KEY);
	}

	public String getNotificationStartupDelay() {
		return appProperties.getProperty(EMAIL_SERVICE_DISPATCHER_NOTIFICATION_DELAY_KEY);
	}

	public String getEmailTemplateDirectory() {
		return appProperties.getProperty(EMAIL_TEMPLATE_DIRECTORY);
	}

	public List<String> getFactoryClassNames() {
		return factoryClassNames;
	}
}
