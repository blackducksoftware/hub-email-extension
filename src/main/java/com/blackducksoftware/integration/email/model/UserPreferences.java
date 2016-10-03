package com.blackducksoftware.integration.email.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class UserPreferences {
	private final Set<String> globalOptedOutEmailAddresses = new HashSet<>();
	private final Map<String, Set<String>> emailAddressToOptedOutTemplates = new HashMap<>();

	public UserPreferences(final ExtensionProperties customerProperties) {
		final Map<String, String> optOutProperties = customerProperties.getOptOutProperties();
		for (final String templateName : optOutProperties.keySet()) {
			final String emailAddressesValue = optOutProperties.get(templateName);
			final String[] emailAddresses = emailAddressesValue.split(",");
			for (String emailAddress : emailAddresses) {
				emailAddress = StringUtils.trimToEmpty(emailAddress);
				if (templateName.contains("all.templates")) {
					globalOptedOutEmailAddresses.add(emailAddress);
				} else {
					if (!emailAddressToOptedOutTemplates.containsKey(emailAddress)) {
						emailAddressToOptedOutTemplates.put(emailAddress, new HashSet<String>());
					}
					emailAddressToOptedOutTemplates.get(emailAddress).add(templateName);
				}
			}
		}
	}

	public boolean isOptedOut(final String emailAddress, final String templateName) {
		if (globalOptedOutEmailAddresses.contains(emailAddress)) {
			return true;
		} else if (emailAddressToOptedOutTemplates.containsKey(emailAddress)) {
			return emailAddressToOptedOutTemplates.get(emailAddress).contains(templateName);
		}

		return false;
	}

}
