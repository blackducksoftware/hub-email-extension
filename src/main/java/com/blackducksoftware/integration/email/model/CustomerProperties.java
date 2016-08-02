package com.blackducksoftware.integration.email.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerProperties {
	private final Logger log = LoggerFactory.getLogger(CustomerProperties.class);

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

	private final List<String> javamailConfigKeys = new ArrayList<>();
	private final Map<String, String> suppliedJavamailConfigProperties = new HashMap<>();

	private final List<String> templateVariableKeys = new ArrayList<>();
	private final Map<String, String> suppliedTemplateVariableProperties = new HashMap<>();

	public CustomerProperties() {
		final String customerPropertiesPath = System.getProperty("customer.properties");
		final File customerPropertiesFile = new File(customerPropertiesPath);
		try (FileInputStream fileInputStream = new FileInputStream(customerPropertiesFile)) {
			final Properties customerProperties = new Properties();
			customerProperties.load(fileInputStream);
			extractProperties(customerProperties);
		} catch (final IOException e) {
			log.error("Couldn't load the customer properties so the application won't function: " + e.getMessage());
		}
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

}
