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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.email.mock.MockLogger;
import com.blackducksoftware.integration.email.mock.MockRestConnection;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.api.component.version.ComponentVersion;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityRequestService;
import com.blackducksoftware.integration.hub.dataservice.notification.model.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubRequestService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.log.IntLogger;

public class NotificationCapacityTest {
    private static final int NOTIFICATION_COUNT = 1000;

    private static final int VULNERABILITY_COUNT = 100;

    private final ProcessorTestUtil testUtil = new ProcessorTestUtil();

    private MetaService metaService;

    @Before
    public void init() throws Exception {
        final RestConnection restConnection = new MockRestConnection(new MockLogger(), null);
        final HubServicesFactory factory = new HubServicesFactory(restConnection);
        final IntLogger logger = new IntBufferedLogger();
        metaService = factory.createMetaService(logger);
    }

    private EmailProcessor createNotificationProcessor() {
        final VulnerabilityRequestService vulnerabilityRequestService = Mockito.mock(VulnerabilityRequestService.class);
        final HubRequestService hubRequestService = Mockito.mock(HubRequestService.class);
        final EmailProcessor processor = new EmailProcessor(hubRequestService, vulnerabilityRequestService, metaService);
        return processor;
    }

    private EmailProcessor createNotificationProcessor(final List<VulnerabilityItem> vulnerabilityList) throws Exception {
        final ComponentVersion compVersion = Mockito.mock(ComponentVersion.class);
        Mockito.when(compVersion.getJson()).thenReturn(createComponentJson());
        final VulnerabilityRequestService vulnerabilityRequestService = Mockito.mock(VulnerabilityRequestService.class);
        final HubRequestService hubRequestService = Mockito.mock(HubRequestService.class);
        Mockito.when(hubRequestService.getItem(Mockito.anyString(), Mockito.eq(ComponentVersion.class))).thenReturn(compVersion);
        Mockito.when(vulnerabilityRequestService.getComponentVersionVulnerabilities(Mockito.anyString())).thenReturn(vulnerabilityList);
        final EmailProcessor processor = new EmailProcessor(hubRequestService, vulnerabilityRequestService, metaService);
        return processor;
    }

    private String createComponentJson() {
        return "{ \"_meta\": { \"href\": \"" + ProcessorTestUtil.COMPONENT_VERSION_URL + "\","
                + "\"links\": [ {"
                + "\"rel\": \"vulnerabilities\","
                + "\"href\": \"" + ProcessorTestUtil.COMPONENT_VERSION_URL + "\"},{"
                + "\"rel\":\"vulnerable-components\","
                + "\"href\": \"" + ProcessorTestUtil.COMPONENT_VERSION_URL + "\""
                + "}]}}";
    }

    private List<VulnerabilitySourceQualifiedId> createVulnerbilityList() {
        final int count = VULNERABILITY_COUNT;
        final List<VulnerabilitySourceQualifiedId> list = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            list.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE + index, ProcessorTestUtil.HIGH_VULN_ID + index));
            list.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE + index, ProcessorTestUtil.MEDIUM_VULN_ID + index));
            list.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE + index, ProcessorTestUtil.LOW_VULN_ID + index));
        }

        return list;
    }

    private SortedSet<NotificationContentItem> createOverrideNotificationCancellationList(final int policyViolationCount)
            throws Exception {
        final int policyCount = policyViolationCount;
        final SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        final DateTime dateTime = DateTime.now();

        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            final PolicyViolationContentItem item = testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName,
                    componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        for (int index = 0; index < policyCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            final PolicyOverrideContentItem item = testUtil.createPolicyOverride(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName,
                    componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        return notificationSet;
    }

    private SortedSet<NotificationContentItem> createClearedNotificationCancellationList(final int policyViolationCount)
            throws Exception {
        final int policyCount = policyViolationCount;
        final SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        final DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            final PolicyViolationContentItem item = testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName,
                    componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        for (int index = 0; index < policyCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            final PolicyViolationClearedContentItem item = testUtil.createPolicyCleared(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName,
                    projectVersion,
                    componentName, componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        return notificationSet;
    }

    private SortedSet<NotificationContentItem> createOverrideNotificationCancellationList()
            throws Exception {
        final int policyCount = NOTIFICATION_COUNT;
        return createOverrideNotificationCancellationList(policyCount);
    }

    private SortedSet<NotificationContentItem> createClearedNotificationCancellationList()
            throws Exception {
        final int policyCount = NOTIFICATION_COUNT;
        return createClearedNotificationCancellationList(policyCount);
    }

    private SortedSet<NotificationContentItem> createVulnerabilityAddedList(final List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        final int policyCount = NOTIFICATION_COUNT;
        final int half = policyCount / 2;
        final int vulnerabilityCount = policyCount;
        final SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        final DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet
                    .add(testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < half; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet
                    .add(testUtil.createPolicyOverride(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }
        for (int index = half - 1; index < policyCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet
                    .add(testUtil.createPolicyCleared(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < vulnerabilityCount; index++) {
            final String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            final String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            final String componentName = ProcessorTestUtil.COMPONENT + index;
            final String versionName = ProcessorTestUtil.VERSION + index;
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet.add(testUtil.createVulnerability(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName, componentVersion, vulnerabilitySourceList, vulnerabilitySourceList, Collections.emptyList()));
            secondOffset++;
        }

        return notificationSet;
    }

    private SortedSet<NotificationContentItem> createComplexNotificationList(final List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        final int policyCount = NOTIFICATION_COUNT;
        final int half = policyCount / 2;
        final int vulnerabilityCount = policyCount;
        final SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        final DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String versionName = ProcessorTestUtil.VERSION;

            if (index % 3 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                versionName = ProcessorTestUtil.VERSION + index;
            }
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet
                    .add(testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < half; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String versionName = ProcessorTestUtil.VERSION + index;
            if (index % 5 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                versionName = ProcessorTestUtil.VERSION + index;
            }
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet
                    .add(testUtil.createPolicyOverride(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }
        for (int index = half - 1; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String versionName = ProcessorTestUtil.VERSION;
            if (index % 7 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                versionName = ProcessorTestUtil.VERSION + index;
            }
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet
                    .add(testUtil.createPolicyCleared(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < vulnerabilityCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String versionName = ProcessorTestUtil.VERSION;
            List<VulnerabilitySourceQualifiedId> addedList = Collections.emptyList();
            List<VulnerabilitySourceQualifiedId> updatedList = Collections.emptyList();
            List<VulnerabilitySourceQualifiedId> deletedList = Collections.emptyList();
            if (index % 2 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                versionName = ProcessorTestUtil.VERSION + index;
                addedList = vulnerabilitySourceList;
            } else {
                updatedList = vulnerabilitySourceList;
            }

            if (index % 4 == 0) {
                deletedList = vulnerabilitySourceList;
            }
            final ComponentVersion componentVersion = Mockito.mock(ComponentVersion.class);
            Mockito.when(componentVersion.getVersionName()).thenReturn(versionName);
            notificationSet.add(testUtil.createVulnerability(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName, componentVersion, addedList, updatedList, deletedList));
            secondOffset++;
        }

        return notificationSet;
    }

    @Test
    public void testProcessorEventCancellationviaOverride() throws Exception {
        System.out.println("Start of Processor event cancellation via override");

        final SortedSet<NotificationContentItem> notificationSet = createOverrideNotificationCancellationList();

        final EmailProcessor processor = createNotificationProcessor();
        final long startTime = System.currentTimeMillis();
        final Collection<ProjectData> projectData = processor.process(notificationSet);
        final long endTime = System.currentTimeMillis();
        final long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertTrue(projectData.isEmpty());
    }

    @Test
    public void testProcessorEventCancellationviaCleared() throws Exception {
        System.out.println("Start of Processor event cancellation via cleared");

        final SortedSet<NotificationContentItem> notificationSet = createClearedNotificationCancellationList();
        final EmailProcessor processor = createNotificationProcessor();
        final long startTime = System.currentTimeMillis();
        final Collection<ProjectData> projectData = processor.process(notificationSet);
        final long endTime = System.currentTimeMillis();
        final long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertTrue(projectData.isEmpty());
    }

    @Test
    public void testProcessorVulnerabilities() throws Exception {
        System.out.println("Start of Processor policy cancellation; vulnerabilities added.");
        final List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList = createVulnerbilityList();
        // setup rest service mocks
        final List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilitySourceList);

        final SortedSet<NotificationContentItem> notificationSet = createVulnerabilityAddedList(vulnerabilitySourceList);
        final EmailProcessor processor = createNotificationProcessor(vulnerabilityList);
        final long startTime = System.currentTimeMillis();
        final Collection<ProjectData> projectData = processor.process(notificationSet);
        final long endTime = System.currentTimeMillis();
        final long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertFalse(projectData.isEmpty());
    }

    @Test
    public void testProcessorComplexNotificationSet() throws Exception {
        System.out.println("Start of Complex notification list.");
        final List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList = createVulnerbilityList();
        // setup rest service mocks
        final List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilitySourceList);

        final SortedSet<NotificationContentItem> notificationSet = createComplexNotificationList(vulnerabilitySourceList);
        final EmailProcessor processor = createNotificationProcessor(vulnerabilityList);
        final long startTime = System.currentTimeMillis();
        final Collection<ProjectData> projectData = processor.process(notificationSet);
        final long endTime = System.currentTimeMillis();
        final long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertFalse(projectData.isEmpty());
    }
}
