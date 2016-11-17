/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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
