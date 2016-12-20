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
package com.blackducksoftware.integration.email.mock;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.notification.NotificationRequestService;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRequestService;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService;
import com.blackducksoftware.integration.hub.api.version.VersionBomPolicyRequestService;
import com.blackducksoftware.integration.hub.dataservice.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservice.notification.item.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubRequestService;
import com.blackducksoftware.integration.log.IntLogger;

public class MockNotificationDataService extends NotificationDataService {
    public MockNotificationDataService(IntLogger logger, RestConnection restConnection, NotificationRequestService notificationRequestService,
            ProjectVersionRequestService projectVersionRequestService, PolicyRequestService policyRequestService,
            VersionBomPolicyRequestService versionBomPolicyRequestService,
            HubRequestService hubRequestService, MetaService metaService) {
        super(logger, restConnection, notificationRequestService, projectVersionRequestService, policyRequestService, versionBomPolicyRequestService,
                hubRequestService, metaService);
    }

    @Override
    public SortedSet<NotificationContentItem> getAllNotifications(final Date startDate, final Date endDate) {
        try {
            return createNotificationList();
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private SortedSet<NotificationContentItem> createNotificationList() throws URISyntaxException {
        final SortedSet<NotificationContentItem> contentList = new TreeSet<>();
        contentList.addAll(createPolicyViolations());
        contentList.addAll(createPolicyOverrides());
        contentList.addAll(createVulnerabilities());
        contentList.addAll(createPolicyViolationsCleared());
        return contentList;
    }

    private List<PolicyViolationContentItem> createPolicyViolations() throws URISyntaxException {
        final List<PolicyViolationContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentUrl = "http://localhost/api/components/" + componentId;
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final List<PolicyRule> policyRuleList = new ArrayList<>();
            final PolicyViolationContentItem item = new PolicyViolationContentItem(new Date(), projectVersion,
                    componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList);
            itemList.add(item);
        }
        return itemList;

    }

    private List<PolicyOverrideContentItem> createPolicyOverrides() throws URISyntaxException {
        final List<PolicyOverrideContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentUrl = "http://localhost/api/components/" + componentId;
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final String firstName = "firstName";
            final String lastName = "lastName";
            final List<PolicyRule> policyRuleList = new ArrayList<>();
            final PolicyOverrideContentItem item = new PolicyOverrideContentItem(new Date(), projectVersion,
                    componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList, firstName,
                    lastName);
            itemList.add(item);
        }
        return itemList;
    }

    private List<VulnerabilityContentItem> createVulnerabilities() throws URISyntaxException {
        final List<VulnerabilityContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final VulnerabilityContentItem item = new VulnerabilityContentItem(new Date(), projectVersion,
                    componentName, componentVersion, componentVersionUrl, createVulnSourceIds(), createVulnSourceIds(),
                    createVulnSourceIds());
            itemList.add(item);
        }
        return itemList;
    }

    private List<VulnerabilitySourceQualifiedId> createVulnSourceIds() {
        final List<VulnerabilitySourceQualifiedId> sourceIdList = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            final VulnerabilitySourceQualifiedId sourceId = new VulnerabilitySourceQualifiedId("source", "id");
            sourceIdList.add(sourceId);
        }
        return sourceIdList;
    }

    private List<PolicyViolationClearedContentItem> createPolicyViolationsCleared() throws URISyntaxException {
        final List<PolicyViolationClearedContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentUrl = "http://localhost/api/components/" + componentId;
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final List<PolicyRule> policyRuleList = new ArrayList<>();
            final PolicyViolationClearedContentItem item = new PolicyViolationClearedContentItem(new Date(),
                    projectVersion, componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList);
            itemList.add(item);
        }
        return itemList;
    }
}
