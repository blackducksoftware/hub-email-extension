package com.blackducksoftware.integration.email.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;

public class CustomProperties {
	public static final String JAVAMAIL_CONFIG_PREFIX = "hub.email.javamail.config.";
	public static final String TEMPLATE_VARIABLE_PREFIX = "hub.email.template.variable.";

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	private final List<String> javamailConfigKeys = new ArrayList<>();
	private final Map<String, String> suppliedJavamailConfigProperties = new HashMap<>();

	private final List<String> templateVariableKeys = new ArrayList<>();
	private final Map<String, String> suppliedTemplateVariableProperties = new HashMap<>();

	@PostConstruct
	public void init() {
		final PropertySources propertySources = configurableEnvironment.getPropertySources();
		for (final PropertySource<?> propertySource : propertySources) {
			if (propertySource instanceof EnumerablePropertySource) {
				final EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) propertySource;
				final String[] propertyNames = enumerable.getPropertyNames();
				for (final String propertyName : propertyNames) {
					final String value = enumerable.getProperty(propertyName).toString();
					if (StringUtils.isNotBlank(value)) {
						if (propertyName.startsWith(JAVAMAIL_CONFIG_PREFIX)) {
							javamailConfigKeys.add(propertyName);
						} else if (propertyName.startsWith(TEMPLATE_VARIABLE_PREFIX)) {
							templateVariableKeys.add(propertyName);
						}
					}
				}
			}
		}

		if (!javamailConfigKeys.isEmpty()) {
			for (final String javamailConfigKey : javamailConfigKeys) {
				final String key = javamailConfigKey.replace(JAVAMAIL_CONFIG_PREFIX, "");
				final String value = configurableEnvironment.getProperty(key);
				suppliedJavamailConfigProperties.put(key, value);
			}
		}

		if (!templateVariableKeys.isEmpty()) {
			for (final String templateVariableKey : templateVariableKeys) {
				final String key = templateVariableKey.replace(TEMPLATE_VARIABLE_PREFIX, "");
				final String value = configurableEnvironment.getProperty(key);
				suppliedTemplateVariableProperties.put(key, value);
			}
		}
	}

	public Map<String, String> getSuppliedJavamailConfigProperties() {
		return suppliedJavamailConfigProperties;
	}

	public Map<String, String> getSuppliedTemplateVariableProperties() {
		return suppliedTemplateVariableProperties;
	}

}
