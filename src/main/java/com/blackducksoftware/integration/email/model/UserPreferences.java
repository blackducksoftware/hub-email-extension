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
