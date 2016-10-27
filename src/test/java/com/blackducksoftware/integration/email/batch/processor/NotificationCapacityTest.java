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
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
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

public class NotificationCapacityTest {

    private DataServicesFactory dataServices;

    private List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList;

    private ProcessorTestUtil testUtil = new ProcessorTestUtil();

    @Before
    public void initTest() throws Exception {
        vulnerabilitySourceList = createVulnerbilityList();
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
    }

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

    private SortedSet<NotificationContentItem> createNotificationCancellationList(int policyViolationCount,
            List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        int policyCount = policyViolationCount;
        int policyOverrideCount = policyCount / 2;
        int policyClearedCount = policyCount / 2;
        int vulnerabilityCount = policyCount;
        SortedSet<NotificationContentItem> notificationList = new TreeSet<>();

        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationList.add(testUtil.createPolicyViolation(new Date(), projectName, projectVersion, componentName, componentVersion));
        }

        for (int index = 0; index < policyOverrideCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationList.add(testUtil.createPolicyOverride(new Date(), projectName, projectVersion, componentName, componentVersion));
        }
        int offset = policyOverrideCount;
        for (int index = 0; index < policyClearedCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + offset + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + offset + index;
            String componentName = ProcessorTestUtil.COMPONENT + offset + index;
            String componentVersion = ProcessorTestUtil.VERSION + offset + index;
            notificationList.add(testUtil.createPolicyCleared(new Date(), projectName, projectVersion, componentName, componentVersion));
        }

        for (int index = 0; index < vulnerabilityCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationList.add(testUtil.createVulnerability(new Date(), projectName, projectVersion,
                    componentName, componentVersion, vulnerabilitySourceList, vulnerabilitySourceList, vulnerabilitySourceList));
        }

        return notificationList;
    }

    private SortedSet<NotificationContentItem> createNotificationCancellationList(List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        int policyCount = 1000000;
        return createNotificationCancellationList(policyCount, vulnerabilitySourceList);
    }

    private SortedSet<NotificationContentItem> createVulnerabilityAddedList(List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        int policyCount = 1000000;
        int policyOverrideCount = policyCount / 2;
        int policyClearedCount = policyCount / 2;
        int vulnerabilityCount = policyCount;
        SortedSet<NotificationContentItem> notificationList = new TreeSet<>();

        for (int index = 0; index < policyCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationList.add(testUtil.createPolicyViolation(new Date(), projectName, projectVersion, componentName, componentVersion));
        }

        for (int index = 0; index < policyOverrideCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationList.add(testUtil.createPolicyOverride(new Date(), projectName, projectVersion, componentName, componentVersion));
        }
        int offset = policyOverrideCount;
        for (int index = 0; index < policyClearedCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + offset + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + offset + index;
            String componentName = ProcessorTestUtil.COMPONENT + offset + index;
            String componentVersion = ProcessorTestUtil.VERSION + offset + index;
            notificationList.add(testUtil.createPolicyCleared(new Date(), projectName, projectVersion, componentName, componentVersion));
        }

        for (int index = 0; index < vulnerabilityCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME + index;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + index;
            String componentName = ProcessorTestUtil.COMPONENT + index;
            String componentVersion = ProcessorTestUtil.VERSION + index;
            notificationList.add(testUtil.createVulnerability(new Date(), projectName, projectVersion,
                    componentName, componentVersion, vulnerabilitySourceList, vulnerabilitySourceList, Collections.emptyList()));
        }

        return notificationList;
    }

    private SortedSet<NotificationContentItem> createComplexNotificationList(List<VulnerabilitySourceQualifiedId> vulnerabilitySourceList)
            throws Exception {
        int policyCount = 1000000;
        int policyOverrideCount = policyCount / 2;
        int policyClearedCount = policyCount / 2;
        int vulnerabilityCount = policyCount;
        SortedSet<NotificationContentItem> notificationList = new TreeSet<>();

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
            notificationList.add(testUtil.createPolicyViolation(new Date(), projectName, projectVersion, componentName, componentVersion));
        }

        for (int index = 0; index < policyOverrideCount; index++) {
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
            notificationList.add(testUtil.createPolicyOverride(new Date(), projectName, projectVersion, componentName, componentVersion));
        }
        int offset = policyOverrideCount;
        for (int index = 0; index < policyClearedCount; index++) {
            String projectName = ProcessorTestUtil.PROJECT_NAME;
            String projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME;
            String componentName = ProcessorTestUtil.COMPONENT;
            String componentVersion = ProcessorTestUtil.VERSION;
            if (index % 7 == 0) {
                projectName = ProcessorTestUtil.PROJECT_NAME + offset + index;
                projectVersion = ProcessorTestUtil.PROJECT_VERSION_NAME + offset + index;
                componentName = ProcessorTestUtil.COMPONENT + offset + index;
                componentVersion = ProcessorTestUtil.VERSION + offset + index;
            }

            notificationList.add(testUtil.createPolicyCleared(new Date(), projectName, projectVersion, componentName, componentVersion));
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

            notificationList.add(testUtil.createVulnerability(new Date(), projectName, projectVersion,
                    componentName, componentVersion, addedList, updatedList, deletedList));
        }

        return notificationList;
    }

    @Test
    public void testProcessorEventCancellation() throws Exception {
        System.out.println("Start of Processor event cancellation");
        SortedSet<NotificationContentItem> notificationSet = createNotificationCancellationList(vulnerabilitySourceList);
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertTrue(projectData.isEmpty());
    }

    @Test
    public void testProcessorVulnerabilities() throws Exception {
        System.out.println("Start of Processor policy cancellation; vulnerabilities added.");
        SortedSet<NotificationContentItem> notificationSet = createVulnerabilityAddedList(vulnerabilitySourceList);
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertFalse(projectData.isEmpty());
    }

    @Test
    public void testProcessorComplexNotificationSet() throws Exception {
        System.out.println("Start of Processor policy cancellation; vulnerabilities added.");
        SortedSet<NotificationContentItem> notificationSet = createComplexNotificationList(vulnerabilitySourceList);
        NotificationProcessor processor = new NotificationProcessor(dataServices);
        long startTime = System.currentTimeMillis();
        Collection<ProjectData> projectData = processor.process(notificationSet);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Start Time (ms) = " + startTime);
        System.out.println("End Time (ms)   = " + endTime);
        System.out.println("Diff (ms)       = " + diff);
        assertFalse(projectData.isEmpty());
    }
}
