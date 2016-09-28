package com.blackducksoftware.integration.email;

public class EmailExtensionConstants {

	public static final String CONFIG_KEY_OPT_IN = "optIn";
	public static final String CONFIG_KEY_FREQUENCY = "emailFrequency";
	public static final String CONFIG_KEY_TEMPLATE_NAME = "templateName";
	public static final String CONFIG_KEY_TRIGGERS = "emailTriggers";

	private EmailExtensionConstants() throws InstantiationException {
		throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
	}
}
