package com.blackducksoftware.integration.email.model;

import java.util.ArrayList;
import java.util.List;

public class EmailData {
	private final List<EmailTarget> emailTargets = new ArrayList<>();

	public void addEmailTarget(final EmailTarget emailTarget) {
		emailTargets.add(emailTarget);
	}

	public List<EmailTarget> getEmailTargets() {
		return emailTargets;
	}

	public EmailData filterOptedOutEmailAddresses(final UserPreferences userPreferences) {
		final EmailData filtered = new EmailData();
		for (final EmailTarget emailTarget : emailTargets) {
			if (!userPreferences.isOptedOut(emailTarget.getEmailAddress(), emailTarget.getTemplateName())) {
				filtered.addEmailTarget(emailTarget);
			}
		}

		return filtered;
	}

}
