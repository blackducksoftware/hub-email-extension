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
package com.blackducksoftware.integration.email.batch.processor;

import java.net.URISyntaxException;
import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;

public class PolicyOverrideEvent extends PolicyEvent {

    public PolicyOverrideEvent(ProcessingAction action, NotificationCategoryEnum categoryType, PolicyOverrideContentItem notificationContent,
            PolicyRule policyRule)
            throws URISyntaxException {
        super(action, categoryType, notificationContent, policyRule);
    }

    @Override
    public Set<ItemEntry> generateDataSet() {
        PolicyOverrideContentItem policyOverride = (PolicyOverrideContentItem) getNotificationContent();
        Set<ItemEntry> dataSet = super.generateDataSet();
        String person = String.join(" ", policyOverride.getFirstName(), policyOverride.getLastName());
        dataSet.add(new ItemEntry(ItemTypeEnum.PERSON.name(), person));
        return dataSet;
    }
}
