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
