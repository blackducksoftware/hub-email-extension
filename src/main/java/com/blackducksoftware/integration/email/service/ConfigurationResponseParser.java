package com.blackducksoftware.integration.email.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.email.dto.ConfigurationElement;
import com.blackducksoftware.integration.email.dto.ConfigurationResponse;
import com.blackducksoftware.integration.email.model.EmailConfiguration;
import com.blackducksoftware.integration.email.model.EmailFrequencyEnum;
import com.blackducksoftware.integration.email.model.EmailTriggerEnum;
import com.google.gson.Gson;

public class ConfigurationResponseParser {
	@Autowired
	private Gson gson;

	public EmailConfiguration fromJson(final String json) {
		final ConfigurationResponse configurationResponse = gson.fromJson(json, ConfigurationResponse.class);

		final EmailConfiguration emailConfiguration = new EmailConfiguration();
		for (final ConfigurationElement element : configurationResponse.getConfigurationElements()) {
			final String fieldName = element.getName();
			if (null == fieldName) {
				throw new RuntimeException("No name property is specifed - json is invalid.");
			}

			List<String> values = element.getValues();
			if (null == values) {
				values = Collections.emptyList();
			}

			if ("optIn".equals(fieldName)) {
				populateOptIn(emailConfiguration, element.getValues().get(0));
			} else if ("templateName".equals(fieldName)) {
				populateTemplateName(emailConfiguration, element.getValues().get(0));
			} else if ("emailFrequency".equals(fieldName)) {
				populateEmailFrequency(emailConfiguration, element.getValues().get(0));
			} else if ("emailTriggers".equals(fieldName)) {
				populateEmailTriggers(emailConfiguration, element.getValues());
			} else if ("emailTriggeringProjects".equals(fieldName)) {
				populateEmailTriggeringProjects(emailConfiguration, element.getValues());
			} else if ("emailTriggeringPolicies".equals(fieldName)) {
				populateEmailTriggeringPolicies(emailConfiguration, element.getValues());
			}
		}

		return emailConfiguration;
	}

	private void populateOptIn(final EmailConfiguration emailConfiguration, final String optInValue) {
		emailConfiguration.setOptIn(Boolean.valueOf(optInValue));
	}

	private void populateTemplateName(final EmailConfiguration emailConfiguration, final String templateNameValue) {
		emailConfiguration.setTemplateName(templateNameValue);
	}

	private void populateEmailFrequency(final EmailConfiguration emailConfiguration, String emailFrequencyValue) {
		emailFrequencyValue = cleanString(emailFrequencyValue);
		for (final EmailFrequencyEnum enumValue : EmailFrequencyEnum.values()) {
			final String cleanedEnumValue = cleanString(enumValue.toString());
			if (emailFrequencyValue.equals(cleanedEnumValue)) {
				emailConfiguration.setEmailFrequency(enumValue);
			}
		}
	}

	private void populateEmailTriggers(final EmailConfiguration emailConfiguration,
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

		emailConfiguration.setEmailTriggers(emailTriggers);
	}

	private void populateEmailTriggeringProjects(final EmailConfiguration emailConfiguration,
			final List<String> emailTriggeringProjectsValue) {
		emailConfiguration.setEmailTriggeringProjects(emailTriggeringProjectsValue);
	}

	private void populateEmailTriggeringPolicies(final EmailConfiguration emailConfiguration,
			final List<String> emailTriggeringPoliciesValue) {
		emailConfiguration.setEmailTriggeringPolicies(emailTriggeringPoliciesValue);
	}

	private String cleanString(final String s) {
		return StringUtils.trimToEmpty(s).replaceAll("[^A-Za-z0-9]", "").toLowerCase().trim();
	}

}
