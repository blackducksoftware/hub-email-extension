package com.blackducksoftware.integration.email;

public enum EmailFrequency {
	DAILY, WEEKLY, MONTHLY, UNKNOWN;

	public static EmailFrequency getEmailFrequency(final String emailFrequencyString) {
		if (emailFrequencyString == null) {
			return EmailFrequency.UNKNOWN;
		}
		EmailFrequency emailFrequencyType;
		try {
			emailFrequencyType = EmailFrequency.valueOf(emailFrequencyString.toUpperCase());
		} catch (final IllegalArgumentException e) {
			// ignore expection
			emailFrequencyType = UNKNOWN;
		}
		return emailFrequencyType;
	}
}
