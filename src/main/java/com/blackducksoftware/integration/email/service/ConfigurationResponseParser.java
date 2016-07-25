package com.blackducksoftware.integration.email.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.dto.ConfigurationElement;
import com.blackducksoftware.integration.email.dto.ConfigurationResponse;
import com.blackducksoftware.integration.email.model.ConfigurationFieldEnum;
import com.blackducksoftware.integration.email.model.EmailFrequencyEnum;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.email.model.EmailTriggerEnum;
import com.google.gson.Gson;

@Component
public class ConfigurationResponseParser {
	private final Logger log = LoggerFactory.getLogger(ConfigurationResponseParser.class);

	@Autowired
	private Gson gson;

	public EmailSystemConfiguration fromJson(final String json) {
		final ConfigurationResponse configurationResponse = gson.fromJson(json, ConfigurationResponse.class);

		final Map<ConfigurationFieldEnum, List<String>> fieldToValues = new HashMap<>();
		for (final ConfigurationElement element : configurationResponse.getConfigurationElements()) {
			final String fieldName = element.getName();
			if (null == fieldName) {
				throw new RuntimeException("No name property is specifed - json is invalid.");
			}
			final ConfigurationFieldEnum field = ConfigurationFieldEnum.valueOf(fieldName);

			fieldToValues.put(field, element.getValues());
		}

		// final SmtpProperties smtpProperties = new SmtpProperties();
		// populateSmtpProperties(smtpProperties, fieldToValues);

		final EmailSystemConfiguration emailSystemConfiguration = new EmailSystemConfiguration();
		// emailSystemConfiguration.setSmtpProperties(smtpProperties);
		populateEmailSystemConfiguration(emailSystemConfiguration, fieldToValues);

		return emailSystemConfiguration;
	}

	private void populateEmailSystemConfiguration(final EmailSystemConfiguration emailSystemConfiguration,
			final Map<ConfigurationFieldEnum, List<String>> fieldToValues) {
		if (checkFieldValue(ConfigurationFieldEnum.emailFrequency, fieldToValues)) {
			populateEmailFrequency(emailSystemConfiguration,
					getString(ConfigurationFieldEnum.emailFrequency, fieldToValues));
		}

		if (checkFieldValue(ConfigurationFieldEnum.emailTriggeringPolicies, fieldToValues)) {
			emailSystemConfiguration.setEmailTriggeringPolicies(
					getStringList(ConfigurationFieldEnum.emailTriggeringPolicies, fieldToValues));
		}

		if (checkFieldValue(ConfigurationFieldEnum.emailTriggeringProjects, fieldToValues)) {
			emailSystemConfiguration.setEmailTriggeringProjects(
					getStringList(ConfigurationFieldEnum.emailTriggeringProjects, fieldToValues));
		}

		if (checkFieldValue(ConfigurationFieldEnum.emailTriggers, fieldToValues)) {
			populateEmailTriggers(emailSystemConfiguration,
					getStringList(ConfigurationFieldEnum.emailTriggers, fieldToValues));
		}

		if (checkFieldValue(ConfigurationFieldEnum.optIn, fieldToValues)) {
			emailSystemConfiguration.setOptIn(getBoolean(ConfigurationFieldEnum.optIn, fieldToValues));
		}

		if (checkFieldValue(ConfigurationFieldEnum.templateName, fieldToValues)) {
			emailSystemConfiguration.setTemplateName(getString(ConfigurationFieldEnum.templateName, fieldToValues));
		}
	}

	private boolean checkFieldValue(final ConfigurationFieldEnum field,
			final Map<ConfigurationFieldEnum, List<String>> fieldToValues) {
		return fieldToValues.containsKey(field) && null != fieldToValues.get(field)
				&& fieldToValues.get(field).size() > 0;
	}

	private boolean getBoolean(final ConfigurationFieldEnum field,
			final Map<ConfigurationFieldEnum, List<String>> fieldToValues) {
		final List<String> values = fieldToValues.get(field);
		try {
			return Boolean.parseBoolean(values.get(0));
		} catch (final Exception e) {
			log.warn(String.format("Couldn't get boolean for %s from %s: %s"), field.toString(),
					StringUtils.join(values, ", "), e.getMessage());
		}
		return false;
	}

	private int getInt(final ConfigurationFieldEnum field,
			final Map<ConfigurationFieldEnum, List<String>> fieldToValues) {
		final List<String> values = fieldToValues.get(field);
		try {
			return Integer.parseInt(values.get(0));
		} catch (final Exception e) {
			log.warn(String.format("Couldn't get int for %s from %s: %s"), field.toString(),
					StringUtils.join(values, ", "), e.getMessage());
		}
		return 0;
	}

	private String getString(final ConfigurationFieldEnum field,
			final Map<ConfigurationFieldEnum, List<String>> fieldToValues) {
		final List<String> values = fieldToValues.get(field);
		try {
			return values.get(0);
		} catch (final Exception e) {
			log.warn(String.format("Couldn't get String for %s from %s: %s"), field.toString(),
					StringUtils.join(values, ", "), e.getMessage());
		}
		return "";
	}

	private List<String> getStringList(final ConfigurationFieldEnum field,
			final Map<ConfigurationFieldEnum, List<String>> fieldToValues) {
		final List<String> values = fieldToValues.get(field);
		try {
			return values;
		} catch (final Exception e) {
			log.warn(String.format("Couldn't get List<String> for %s from %s: %s"), field.toString(),
					StringUtils.join(values, ", "), e.getMessage());
		}
		return Collections.emptyList();
	}

	private void populateEmailFrequency(final EmailSystemConfiguration emailSystemConfiguration,
			String emailFrequencyValue) {
		emailFrequencyValue = cleanString(emailFrequencyValue);
		for (final EmailFrequencyEnum enumValue : EmailFrequencyEnum.values()) {
			final String cleanedEnumValue = cleanString(enumValue.toString());
			if (emailFrequencyValue.equals(cleanedEnumValue)) {
				emailSystemConfiguration.setEmailFrequency(enumValue);
			}
		}
	}

	private void populateEmailTriggers(final EmailSystemConfiguration emailSystemConfiguration,
			final List<String> emailTriggersValue) {
		final List<EmailTriggerEnum> emailTriggers = new ArrayList<>();
		for (String emailTriggerValue : emailTriggersValue) {
			emailTriggerValue = cleanString(emailTriggerValue);
			for (final EmailTriggerEnum enumValue : EmailTriggerEnum.values()) {
				final String cleanedEnumValue = cleanString(enumValue.toString());
				if (emailTriggerValue.equals(cleanedEnumValue)) {
					emailTriggers.add(enumValue);
				}
			}
		}

		emailSystemConfiguration.setEmailTriggers(emailTriggers);
	}

	private String cleanString(final String s) {
		return StringUtils.trimToEmpty(s).replaceAll("[^A-Za-z0-9]", "").toLowerCase().trim();
	}

}
