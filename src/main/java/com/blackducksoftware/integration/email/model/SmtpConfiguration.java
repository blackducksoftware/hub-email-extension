package com.blackducksoftware.integration.email.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.stereotype.Component;

@Component
public class SmtpConfiguration {
	public static final String JAVAMAIL_SMTP_CONFIG_PREFIX = "hub.email.javamail.config.";

	// common javamail properties
	public static final String JAVAMAIL_HOST_KEY = "mail.smtp.host";
	public static final String JAVAMAIL_PORT_KEY = "mail.smtp.port";
	public static final String JAVAMAIL_AUTH_KEY = "mail.smtp.auth";
	public static final String JAVAMAIL_USER_KEY = "mail.smtp.user";

	// not a javamail property, but we are going to piggy-back on the
	// auto-parsing for javamail properties to get the password
	public static final String JAVAMAIL_PASSWORD_KEY = "mail.smtp.password";

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	private final List<String> javamailPropertyKeys = new ArrayList<>();
	private final Map<String, String> suppliedSmtpProperties = new HashMap<>();

	@PostConstruct
	public void init() {
		final PropertySources propertySources = configurableEnvironment.getPropertySources();
		for (final PropertySource<?> propertySource : propertySources) {
			if (propertySource instanceof EnumerablePropertySource) {
				final EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) propertySource;
				final String[] propertyNames = enumerable.getPropertyNames();
				for (final String propertyName : propertyNames) {
					if (propertyName.startsWith(JAVAMAIL_SMTP_CONFIG_PREFIX)) {
						final String value = enumerable.getProperty(propertyName).toString();
						if (StringUtils.isNotBlank(value)) {
							javamailPropertyKeys.add(propertyName);
						}
					}
				}
			}
		}

		if (suppliedSmtpProperties.isEmpty() && !javamailPropertyKeys.isEmpty()) {
			for (final String key : javamailPropertyKeys) {
				final String value = configurableEnvironment.getProperty(key);
				final String smtpKey = key.replace(JAVAMAIL_SMTP_CONFIG_PREFIX, "");
				suppliedSmtpProperties.put(smtpKey, value);
			}
		}
	}

	public Map<String, String> getPropertiesForSession() {
		return suppliedSmtpProperties;
	}

	public String getHost() {
		return suppliedSmtpProperties.get(JAVAMAIL_HOST_KEY);
	}

	public int getPort() {
		return NumberUtils.toInt(suppliedSmtpProperties.get(JAVAMAIL_PORT_KEY));
	}

	public boolean isAuth() {
		return Boolean.parseBoolean(suppliedSmtpProperties.get(JAVAMAIL_AUTH_KEY));
	}

	public String getUsername() {
		return suppliedSmtpProperties.get(JAVAMAIL_USER_KEY);
	}

	public String getPassword() {
		return suppliedSmtpProperties.get(JAVAMAIL_PASSWORD_KEY);
	}

}
