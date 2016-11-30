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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyExpressions;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.meta.MetaInformation;

public class NotificationEventTest {
    private static final String POLICY_RULE_URL = "http://a.hub.server/policy/url";

    private static final String COMPONENT_VERSION = "componentVersion";

    private static final String COMPONENT_URL = "http://a.hub.server/component/link";

    private static final String SOURCE = "source";

    private static final String VULN_ID2 = "vuln_id2";

    private static final String VULN_ID1 = "vuln_id1";

    private static final String PROJECT_VERSION_URL = "http://a.hub.server/project/version/link";

    private static final String COMPONENT_VERSION_URL = "http://a.hub.server/component/version/link";

    private static final String COMPONENT_NAME = "componentName";

    private static final String PROJECT_VERSION = "projectVersion";

    private static final String PROJECT_NAME = "project1";

    @Test
    public void testVulnerabilityConstructor() throws Exception {
        final ProcessingAction action = ProcessingAction.ADD;

        final String projectName = PROJECT_NAME;
        final String projectVersionName = PROJECT_VERSION;
        final String componentName = COMPONENT_NAME;
        final String componentVersion = "";
        final String componentNameVersionUrl = COMPONENT_VERSION_URL;
        final ProjectVersion projectVersion = new ProjectVersion();
        projectVersion.setProjectName(projectName);
        projectVersion.setProjectVersionName(projectVersionName);
        projectVersion.setUrl(PROJECT_VERSION_URL);
        final NotificationCategoryEnum categoryType = NotificationCategoryEnum.VULNERABILITY;
        final Set<ItemEntry> dataSet = new HashSet<>();
        dataSet.add(new ItemEntry(ItemTypeEnum.COMPONENT.name(), "item"));
        dataSet.add(new ItemEntry(ItemTypeEnum.RULE.name(), "rule"));
        final Set<String> vulnerabilityIdSet = new HashSet<>();
        vulnerabilityIdSet.add(VULN_ID1);
        vulnerabilityIdSet.add(VULN_ID2);
        vulnerabilityIdSet.add(VULN_ID1);
        final List<VulnerabilitySourceQualifiedId> vulnList = new ArrayList<>();
        vulnerabilityIdSet.forEach(s -> vulnList.add(new VulnerabilitySourceQualifiedId(SOURCE, s)));

        final VulnerabilityContentItem vulnerabilityContentItem = new VulnerabilityContentItem(new Date(), projectVersion, componentName, componentVersion,
                componentNameVersionUrl, vulnList, vulnList, vulnList);

        final VulnerabilityEvent event = new VulnerabilityEvent(action, categoryType, vulnerabilityContentItem, vulnerabilityIdSet);

        assertEquals(action, event.getAction());
        assertEquals(projectName, event.getNotificationContent().getProjectVersion().getProjectName());
        assertEquals(projectVersionName, event.getNotificationContent().getProjectVersion().getProjectVersionName());
        assertEquals(componentName, event.getNotificationContent().getComponentName());
        assertEquals("", event.getNotificationContent().getComponentVersion());
        assertNotNull(event.getEventKey());
    }

    @Test
    public void testPolicyConstructor() throws Exception {

        final ProcessingAction action = ProcessingAction.ADD;

        final String projectName = PROJECT_NAME;
        final String projectVersionName = PROJECT_VERSION;
        final String componentName = COMPONENT_NAME;
        final String componentVersion = COMPONENT_VERSION;
        final String componentUrl = COMPONENT_URL;
        final String componentNameVersionUrl = COMPONENT_VERSION_URL;
        final ProjectVersion projectVersion = new ProjectVersion();
        projectVersion.setProjectName(projectName);
        projectVersion.setProjectVersionName(projectVersionName);
        projectVersion.setUrl(PROJECT_VERSION_URL);
        final NotificationCategoryEnum categoryType = NotificationCategoryEnum.POLICY_VIOLATION;
        final Set<ItemEntry> dataSet = new HashSet<>();
        dataSet.add(new ItemEntry(ItemTypeEnum.COMPONENT.name(), "item"));
        dataSet.add(new ItemEntry(ItemTypeEnum.RULE.name(), "rule"));
        final MetaInformation meta = new MetaInformation(Collections.emptyList(), POLICY_RULE_URL, Collections.emptyList());
        final List<PolicyRule> ruleList = new ArrayList<>(1);
        ruleList.add(new PolicyRule(meta, "", "description", Boolean.TRUE, Boolean.TRUE, new PolicyExpressions("expression", Collections.emptyList()),
                "", "", "", "a user"));

        final PolicyViolationContentItem notificationContent = new PolicyViolationContentItem(new Date(), projectVersion, componentName, componentVersion,
                componentUrl,
                componentNameVersionUrl, ruleList);
        final PolicyEvent event = new PolicyEvent(action, categoryType, notificationContent, ruleList.get(0));

        assertEquals(action, event.getAction());
        assertEquals(projectName, event.getNotificationContent().getProjectVersion().getProjectName());
        assertEquals(projectVersionName, event.getNotificationContent().getProjectVersion().getProjectVersionName());
        assertEquals(componentName, event.getNotificationContent().getComponentName());
        assertEquals(componentVersion, event.getNotificationContent().getComponentVersion());
        assertNotNull(event.getEventKey());
    }

    @Test
    public void testPolicyOverrideConstructor() throws Exception {

        final ProcessingAction action = ProcessingAction.ADD;

        final String projectName = PROJECT_NAME;
        final String projectVersionName = PROJECT_VERSION;
        final String componentName = COMPONENT_NAME;
        final String componentVersion = COMPONENT_VERSION;
        final String componentUrl = COMPONENT_URL;
        final String componentNameVersionUrl = COMPONENT_VERSION_URL;
        final ProjectVersion projectVersion = new ProjectVersion();
        projectVersion.setProjectName(projectName);
        projectVersion.setProjectVersionName(projectVersionName);
        projectVersion.setUrl(PROJECT_VERSION_URL);
        final NotificationCategoryEnum categoryType = NotificationCategoryEnum.POLICY_VIOLATION;
        final Set<ItemEntry> dataSet = new HashSet<>();
        dataSet.add(new ItemEntry(ItemTypeEnum.COMPONENT.name(), "item"));
        dataSet.add(new ItemEntry(ItemTypeEnum.RULE.name(), "rule"));
        final MetaInformation meta = new MetaInformation(Collections.emptyList(), POLICY_RULE_URL, Collections.emptyList());
        final List<PolicyRule> ruleList = new ArrayList<>(1);
        ruleList.add(new PolicyRule(meta, "", "description", Boolean.TRUE, Boolean.TRUE, new PolicyExpressions("expression", Collections.emptyList()),
                "", "", "", "a user"));

        final PolicyOverrideContentItem notificationContent = new PolicyOverrideContentItem(new Date(), projectVersion, componentName, componentVersion,
                componentUrl,
                componentNameVersionUrl, ruleList, "firstName", "lastName");
        final PolicyEvent event = new PolicyEvent(action, categoryType, notificationContent, ruleList.get(0));

        assertEquals(action, event.getAction());
        assertEquals(projectName, event.getNotificationContent().getProjectVersion().getProjectName());
        assertEquals(projectVersionName, event.getNotificationContent().getProjectVersion().getProjectVersionName());
        assertEquals(componentName, event.getNotificationContent().getComponentName());
        assertEquals(componentVersion, event.getNotificationContent().getComponentVersion());
        assertNotNull(event.getEventKey());
    }
}
