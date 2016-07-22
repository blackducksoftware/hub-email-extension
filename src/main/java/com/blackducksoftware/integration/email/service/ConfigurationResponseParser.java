package com.blackducksoftware.integration.email.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.email.dto.ConfigurationElement;
import com.blackducksoftware.integration.email.dto.ConfigurationResponse;
import com.blackducksoftware.integration.email.model.EmailFrequencyEnum;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.email.model.EmailTriggerEnum;
import com.google.gson.Gson;

public class ConfigurationResponseParser {
	@Autowired
	private Gson gson;

	public EmailSystemConfiguration fromJson(final String json) {
		final ConfigurationResponse configurationResponse = gson.fromJson(json, ConfigurationResponse.class);

		final EmailSystemConfiguration emailSystemConfiguration = new EmailSystemConfiguration();
		for (final ConfigurationElement element : configurationResponse.getConfigurationElements()) {
			final String fieldName = element.getName();
			if (null == fieldName) {
				throw new RuntimeException("No name property is specifed - json is invalid.");
			}

			List<String> values = element.getValues();
			if (null == values) {
				values = Collections.emptyList();
			}

			populateField(emailSystemConfiguration, element, fieldName);
		}

		return emailSystemConfiguration;
	}

	private void populateField(final EmailSystemConfiguration emailSystemConfiguration,
			final ConfigurationElement element, final String fieldName) {
		if ("optIn".equals(fieldName)) {
			populateOptIn(emailSystemConfiguration, element.getValues().get(0));
		} else if ("templateName".equals(fieldName)) {
			populateTemplateName(emailSystemConfiguration, element.getValues().get(0));
		} else if ("emailFrequency".equals(fieldName)) {
			populateEmailFrequency(emailSystemConfiguration, element.getValues().get(0));
		} else if ("emailTriggers".equals(fieldName)) {
			populateEmailTriggers(emailSystemConfiguration, element.getValues());
		} else if ("emailTriggeringProjects".equals(fieldName)) {
			populateEmailTriggeringProjects(emailSystemConfiguration, element.getValues());
		} else if ("emailTriggeringPolicies".equals(fieldName)) {
			populateEmailTriggeringPolicies(emailSystemConfiguration, element.getValues());
		}
	}

	private void populateOptIn(final EmailSystemConfiguration emailSystemConfiguration, final String optInValue) {
		emailSystemConfiguration.setOptIn(Boolean.valueOf(optInValue));
	}

	private void populateTemplateName(final EmailSystemConfiguration emailSystemConfiguration,
			final String templateNameValue) {
		emailSystemConfiguration.setTemplateName(templateNameValue);
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

	private void populateEmailTriggeringProjects(final EmailSystemConfiguration emailSystemConfiguration,
			final List<String> emailTriggeringProjectsValue) {
		emailSystemConfiguration.setEmailTriggeringProjects(emailTriggeringProjectsValue);
	}

	private void populateEmailTriggeringPolicies(final EmailSystemConfiguration emailSystemConfiguration,
			final List<String> emailTriggeringPoliciesValue) {
		emailSystemConfiguration.setEmailTriggeringPolicies(emailTriggeringPoliciesValue);
	}

	private String cleanString(final String s) {
		return StringUtils.trimToEmpty(s).replaceAll("[^A-Za-z0-9]", "").toLowerCase().trim();
	}

}
