package com.blackducksoftware.integration.email.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

public class UserPreferencesTest {
    @Test
    public void testOptingOutOfAllTemplates() {
        final Properties properties = new Properties();
        properties.setProperty("hub.email.user.preference.opt.out.all.templates", "ekerwin@blackducksoftware.com");
        final ExtensionProperties customerProperties = new ExtensionProperties(properties);

        final UserPreferences userPreferences = new UserPreferences(customerProperties);
        assertTrue(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "anything"));
        assertTrue(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "specificTemplate.ftl"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "anything"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "specificTemplate.ftl"));
    }

    @Test
    public void testOptingOutOfSpecificTemplate() {
        final Properties properties = new Properties();
        properties.setProperty("hub.email.user.preference.opt.out.specificTemplate.ftl",
                "ekerwin@blackducksoftware.com");
        final ExtensionProperties customerProperties = new ExtensionProperties(properties);

        final UserPreferences userPreferences = new UserPreferences(customerProperties);
        assertFalse(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "anything"));
        assertTrue(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "specificTemplate.ftl"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "anything"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "specificTemplate.ftl"));
    }

    @Test
    public void testMultipleOptingOutOfAllTemplates() {
        final Properties properties = new Properties();
        properties.setProperty("hub.email.user.preference.opt.out.all.templates",
                "ekerwin@blackducksoftware.com, psantos@blackducksoftware.com");
        final ExtensionProperties customerProperties = new ExtensionProperties(properties);

        final UserPreferences userPreferences = new UserPreferences(customerProperties);
        assertTrue(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "anything"));
        assertTrue(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "specificTemplate.ftl"));
        assertTrue(userPreferences.isOptedOut("psantos@blackducksoftware.com", "anything"));
        assertTrue(userPreferences.isOptedOut("psantos@blackducksoftware.com", "specificTemplate.ftl"));
    }

    @Test
    public void testMultipleOptingOutOfSpecificTemplate() {
        final Properties properties = new Properties();
        properties.setProperty("hub.email.user.preference.opt.out.specificTemplate.ftl",
                "ekerwin@blackducksoftware.com,psantos@blackducksoftware.com");
        final ExtensionProperties customerProperties = new ExtensionProperties(properties);

        final UserPreferences userPreferences = new UserPreferences(customerProperties);
        assertFalse(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "anything"));
        assertTrue(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "specificTemplate.ftl"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "anything"));
        assertTrue(userPreferences.isOptedOut("psantos@blackducksoftware.com", "specificTemplate.ftl"));
    }

    @Test
    public void testNoOptOut() {
        final Properties properties = new Properties();
        final ExtensionProperties customerProperties = new ExtensionProperties(properties);

        final UserPreferences userPreferences = new UserPreferences(customerProperties);
        assertFalse(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "anything"));
        assertFalse(userPreferences.isOptedOut("ekerwin@blackducksoftware.com", "specificTemplate.ftl"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "anything"));
        assertFalse(userPreferences.isOptedOut("psantos@blackducksoftware.com", "specificTemplate.ftl"));
    }

}
