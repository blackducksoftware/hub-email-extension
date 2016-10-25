/*
 * Copyright (C) 2016 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
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
