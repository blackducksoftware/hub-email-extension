package com.blackducksoftware.integration.email;

public class EmailExtensionConstants {

    public static final String CONFIG_KEY_OPT_IN = "optIn";

    public static final String CONFIG_KEY_FREQUENCY = "emailFrequency";

    public static final String CONFIG_KEY_TEMPLATE_NAME = "templateName";

    public static final String CONFIG_KEY_TRIGGERS = "emailTriggers";

    public static final String SYSTEM_PROPERTY_KEY_APP_HOME = "APP_HOME";

    public static final String CONTEXT_ATTRIBUTE_KEY_TEST_NOTIFIER = "blackduck-test-email-notifier";

    private EmailExtensionConstants() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }
}
