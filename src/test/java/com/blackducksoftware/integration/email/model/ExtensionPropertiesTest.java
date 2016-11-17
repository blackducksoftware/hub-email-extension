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

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class ExtensionPropertiesTest {

    private static final String LOCAL_VALUE = "Local Value";

    private static final String DEFAULT_VALUE = "Default Value";

    private static final String COMMON_KEY = "common_key";

    private static final String UNIQUE_KEY_DEFAULT = "default_unique_key";

    private static final String UNIQUE_KEY_LOCAL = "local_unique_key";

    private Properties createDefaults() {
        final Properties props = new Properties();
        props.put(COMMON_KEY, DEFAULT_VALUE);
        props.put(UNIQUE_KEY_DEFAULT, DEFAULT_VALUE);
        return props;
    }

    private Properties createLocal() {
        final Properties props = new Properties();
        props.put(COMMON_KEY, LOCAL_VALUE);
        props.put(UNIQUE_KEY_LOCAL, LOCAL_VALUE);
        return props;
    }

    @Test
    public void testPropertyOverride() {
        final Properties defaults = createDefaults();
        final Properties local = createLocal();

        final ExtensionProperties extProps = new ExtensionProperties(defaults, local);
        final Properties appProps = extProps.getAppProperties();
        assertEquals(3, extProps.getAppProperties().size());
        assertEquals(appProps.get(UNIQUE_KEY_DEFAULT), DEFAULT_VALUE);
        assertEquals(appProps.get(COMMON_KEY), LOCAL_VALUE);
        assertEquals(appProps.get(UNIQUE_KEY_LOCAL), LOCAL_VALUE);
    }
}
