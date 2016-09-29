package com.blackducksoftware.integration.email;

public enum EmailFrequencyCategory {
	DAILY, WEEKLY, MONTHLY, UNKNOWN;

	public static EmailFrequencyCategory getEmailFrequency(final String emailFrequencyString) {
		if (emailFrequencyString == null) {
			return EmailFrequencyCategory.UNKNOWN;
		}
		EmailFrequencyCategory emailFrequencyType;
		try {
			emailFrequencyType = EmailFrequencyCategory.valueOf(emailFrequencyString.toUpperCase());
		} catch (final IllegalArgumentException e) {
			// ignore expection
			emailFrequencyType = UNKNOWN;
		}
		return emailFrequencyType;
	}
}
