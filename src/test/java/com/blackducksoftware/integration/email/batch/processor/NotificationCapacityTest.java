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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.api.component.ComponentVersion;
import com.blackducksoftware.integration.hub.api.component.ComponentVersionRestService;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityRestService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;

public class NotificationCapacityTest {

    private static final int NOTIFICATION_COUNT = 1000;

    private DataServicesFactory dataServices;

    private ProcessorTestUtil testUtil = new ProcessorTestUtil();

    private List<VulnerabilitySourceQualifiedId> createVulnerbilityList() {
        int count = 100;
        List<VulnerabilitySourceQualifiedId> list = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            list.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE + index, ProcessorTestUtil.HIGH_VULN_ID + index));
            list.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE + index, ProcessorTestUtil.MEDIUM_VULN_ID + index));
            list.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE + index, ProcessorTestUtil.LOW_VULN_ID + index));
        }

        return list;
    }

    private SortedSet<NotificationContentItem> createOverrideNotificationCancellationList(int policyViolationCount)
            throws Exception {
        int policyCount = policyViolationCount;
        SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            PolicyViolationContentItem item = testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName,
                    componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            PolicyOverrideContentItem item = testUtil.createPolicyOverride(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName,
                    componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        return notificationSet;
    }

    private SortedSet<NotificationContentItem> createClearedNotificationCancellationList(int policyViolationCount)
            throws Exception {
        int policyCount = policyViolationCount;
        SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            PolicyViolationContentItem item = testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName,
                    componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            PolicyViolationClearedContentItem item = testUtil.createPolicyCleared(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName, componentVersion);
            notificationSet.add(item);
            secondOffset++;
        }

        return notificationSet;
    }

    private SortedSet<NotificationContentItem> createOverrideNotificationCancellationList()
            throws Exception {
        int policyCount = NOTIFICATION_COUNT;
        return createOverrideNotificationCancellationList(policyCount);
    }

    private SortedSet<NotificationContentItem> createClearedNotificationCancellationList()
            throws Exception {
        int policyCount = NOTIFICATION_COUNT;
        return createClearedNotificationCancellationList(policyCount);
    }

    private SortedSet<NotificationContentItem> createVulnerabilityAddedList(List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        int policyCount = NOTIFICATION_COUNT;
        int half = policyCount / 2;
        int vulnerabilityCount = policyCount;
        SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationSet
                    .add(testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < half; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationSet
                    .add(testUtil.createPolicyOverride(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }
        for (int index = half - 1; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationSet
                    .add(testUtil.createPolicyCleared(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < vulnerabilityCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationSet.add(testUtil.createVulnerability(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName, componentVersion, vulnerabilitySourceList, vulnerabilitySourceList, Collections.emptyList()));
            secondOffset++;
        }

        return notificationSet;
    }

    private SortedSet<NotificationContentItem> createComplexNotificationList(List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        int policyCount = NOTIFICATION_COUNT;
        int half = policyCount / 2;
        int vulnerabilityCount = policyCount;
        SortedSet<NotificationContentItem> notificationSet = new TreeSet<>();
        DateTime dateTime = DateTime.now();
        int secondOffset = 0;
        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String componentVersion = ProcessorTestUtil.VERSION;

            if (index % 3 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                componentVersion = ProcessorTestUtil.VERSION + index;
            }
            notificationSet
                    .add(testUtil.createPolicyViolation(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < half; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            if (index % 5 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                componentVersion = ProcessorTestUtil.VERSION + index;
            }
            notificationSet
                    .add(testUtil.createPolicyOverride(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }
        for (int index = half - 1; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String componentVersion = ProcessorTestUtil.VERSION;
            if (index % 7 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                componentVersion = ProcessorTestUtil.VERSION + index;
            }

            notificationSet
                    .add(testUtil.createPolicyCleared(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion, componentName,
                            componentVersion));
            secondOffset++;
        }

        for (int index = 0; index < vulnerabilityCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String componentVersion = ProcessorTestUtil.VERSION;
            List<VulnerabilitySourceQualifiedId> addedList = Collections.emptyList();
            List<VulnerabilitySourceQualifiedId> updatedList = Collections.emptyList();
            List<VulnerabilitySourceQualifiedId> deletedList = Collections.emptyList();
            if (index % 2 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
                componentName = ProcessorTestUtil.COMPONENT + index;
                componentVersion = ProcessorTestUtil.VERSION + index;
                addedList = vulnerabilitySourceList;
            } else {
                updatedList = vulnerabilitySourceList;
            }

            if (index % 4 == 0) {
                deletedList = vulnerabilitySourceList;
            }

            notificationSet.add(testUtil.createVulnerability(dateTime.plusSeconds(1 + secondOffset).toDate(), projectName, projectVersion,
                    componentName, componentVersion, addedList, updatedList, deletedList));
            secondOffset++;
        }

        return notificationSet;
    }

    @Test
    public void testProcessorEventCancellationviaOverride() throws Exception {
        System.out.println("Start of Processor event cancellation via override");

        SortedSet<NotificationContentItem> notificationSet = createOverrideNotificationCancellationList();
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        System.out.println("ProjectData     = " + projectData);
        assertTrue(projectData.isEmpty());
    }

    @Test
    public void testProcessorEventCancellationviaCleared() throws Exception {
        System.out.println("Start of Processor event cancellation via cleared");

        SortedSet<NotificationContentItem> notificationSet = createClearedNotificationCancellationList();
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        System.out.println("ProjectData     = " + projectData);
        assertTrue(projectData.isEmpty());
    }

    @Test
    public void testProcessorVulnerabilities() throws Exception {
        System.out.println("Start of Processor policy cancellation; vulnerabilities added.");
        List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList = createVulnerbilityList();
        // setup rest service mocks
        List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilitySourceList);
        dataServices = Mockito.mock(DataServicesFactory.class);
        final VulnerabilityRestService vulnRestService = Mockito.mock(VulnerabilityRestService.class);
        final ComponentVersion compVersion = Mockito.mock(ComponentVersion.class);
        Mockito.when(compVersion.getLink(Mockito.anyString())).thenReturn(ProcessorTestUtil.COMPONENT_VERSION_URL);
        final ComponentVersionRestService compVerRestService = Mockito.mock(ComponentVersionRestService.class);
        Mockito.when(compVerRestService.getItem(Mockito.anyString())).thenReturn(compVersion);
        Mockito.when(vulnRestService.getComponentVersionVulnerabilities(Mockito.anyString())).thenReturn(vulnerabilityList);
        Mockito.when(dataServices.getComponentVersionRestService()).thenReturn(compVerRestService);
        Mockito.when(dataServices.getVulnerabilityRestService()).thenReturn(vulnRestService);

        SortedSet<NotificationContentItem> notificationSet = createVulnerabilityAddedList(vulnerabilitySourceList);
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        System.out.println("ProjectData     = " + projectData);
        assertFalse(projectData.isEmpty());
    }

    @Test
    public void testProcessorComplexNotificationSet() throws Exception {
        System.out.println("Start of Complex notification list.");
        List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList = createVulnerbilityList();
        // setup rest service mocks
        List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilitySourceList);
        dataServices = Mockito.mock(DataServicesFactory.class);
        final VulnerabilityRestService vulnRestService = Mockito.mock(VulnerabilityRestService.class);
        final ComponentVersion compVersion = Mockito.mock(ComponentVersion.class);
        Mockito.when(compVersion.getLink(Mockito.anyString())).thenReturn(ProcessorTestUtil.COMPONENT_VERSION_URL);
        final ComponentVersionRestService compVerRestService = Mockito.mock(ComponentVersionRestService.class);
        Mockito.when(compVerRestService.getItem(Mockito.anyString())).thenReturn(compVersion);
        Mockito.when(vulnRestService.getComponentVersionVulnerabilities(Mockito.anyString())).thenReturn(vulnerabilityList);
        Mockito.when(dataServices.getComponentVersionRestService()).thenReturn(compVerRestService);
        Mockito.when(dataServices.getVulnerabilityRestService()).thenReturn(vulnRestService);

        SortedSet<NotificationContentItem> notificationSet = createComplexNotificationList(vulnerabilitySourceList);
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        System.out.println("ProjectData     = " + projectData);
        assertFalse(projectData.isEmpty());
    }
}
