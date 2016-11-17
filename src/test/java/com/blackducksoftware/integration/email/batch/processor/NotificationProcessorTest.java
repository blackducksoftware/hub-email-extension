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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.api.component.version.ComponentVersion;
import com.blackducksoftware.integration.hub.api.component.version.ComponentVersionRestService;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityRestService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

public class NotificationProcessorTest {

    private NotificationProcessor processor;

    private DataServicesFactory dataServices;

    private ProcessorTestUtil testUtil = new ProcessorTestUtil();

    @Before
    public void initTest() throws Exception {
        dataServices = Mockito.mock(DataServicesFactory.class);
        processor = new NotificationProcessor(dataServices);
    }

    public void initVulnerabilityTest(List<VulnerabilityItem> vulnerabilityList) throws Exception {
        final VulnerabilityRestService vulnRestService = Mockito.mock(VulnerabilityRestService.class);
        final ComponentVersion compVersion = Mockito.mock(ComponentVersion.class);
        Mockito.when(compVersion.getLink(Mockito.anyString())).thenReturn(ProcessorTestUtil.COMPONENT_VERSION_URL);
        final ComponentVersionRestService compVerRestService = Mockito.mock(ComponentVersionRestService.class);
        Mockito.when(compVerRestService.getItem(Mockito.anyString())).thenReturn(compVersion);
        Mockito.when(vulnRestService.getComponentVersionVulnerabilities(Mockito.anyString())).thenReturn(vulnerabilityList);
        Mockito.when(dataServices.getComponentVersionRestService()).thenReturn(compVerRestService);
        Mockito.when(dataServices.getVulnerabilityRestService()).thenReturn(vulnRestService);
    }

    private void assertPolicyDataValid(final Collection<ProjectData> projectList, NotificationCategoryEnum categoryType) {
        for (final ProjectData project : projectList) {
            assertEquals(ProcessorTestUtil.PROJECT_NAME, project.getProjectName());
            assertEquals(ProcessorTestUtil.PROJECT_VERSION_NAME, project.getProjectVersion());

            for (final CategoryData category : project.getCategoryMap().values()) {
                assertEquals(categoryType.name(), category.getCategoryKey());
                int count = category.getItemList().size();
                for (int index = 0; index < count; index++) {
                    ItemData itemData = category.getItemList().get(index);
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));

                    final ItemEntry ruleKey = new ItemEntry(ItemTypeEnum.RULE.name(), ProcessorTestUtil.PREFIX_RULE + (index + 1));
                    assertTrue(dataSet.contains(ruleKey));
                }
            }
        }
    }

    @Test
    public void testPolicyViolationAdd() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        notifications.add(
                testUtil.createPolicyViolation(new Date(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT,
                        ProcessorTestUtil.VERSION));
        final Collection<ProjectData> projectList = processor.process(notifications);

        assertPolicyDataValid(projectList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testPolicyViolationOverride() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        notifications.add(
                testUtil.createPolicyOverride(new Date(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT,
                        ProcessorTestUtil.VERSION));
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertFalse(projectList.isEmpty());
        assertPolicyDataValid(projectList, NotificationCategoryEnum.POLICY_VIOLATION_OVERRIDE);
    }

    @Test
    public void testPolicyViolationCleared() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        notifications.add(
                testUtil.createPolicyCleared(new Date(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT,
                        ProcessorTestUtil.VERSION));
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertFalse(projectList.isEmpty());
        assertPolicyDataValid(projectList, NotificationCategoryEnum.POLICY_VIOLATION_CLEARED);
    }

    @Test
    public void testPolicyViolationAndOverride() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyOverride = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyOverride);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertTrue(projectList.isEmpty());
    }

    @Test
    public void testPolicyViolationAndCleared() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyCleared);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertTrue(projectList.isEmpty());
    }

    @Test
    public void testPolicyViolationAndClearedAndViolated() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyCleared);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertPolicyDataValid(projectList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testPolicyViolationAndOverrideAndViolated() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyCleared = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyCleared);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertPolicyDataValid(projectList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testComplexPolicyOverride() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyOverride = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyOverride);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyCleared);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertPolicyDataValid(projectList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testVulnerabilityAdded() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilities);
        initVulnerabilityTest(vulnerabilityList);
        final DateTime dateTime = new DateTime();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, vulnerabilities, Collections.emptyList(),
                Collections.emptyList());
        notifications.add(vulnerability);
        final Collection<ProjectData> projectList = processor.process(notifications);

        for (final ProjectData projectData : projectList) {
            assertEquals(3, projectData.getCategoryMap().size());
            for (final CategoryData category : projectData.getCategoryMap().values()) {
                for (final ItemData itemData : category.getItemList()) {
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));
                }
            }
        }
    }

    @Test
    public void testVulnerabilityUpdated() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilities);
        initVulnerabilityTest(vulnerabilityList);

        final DateTime dateTime = new DateTime();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, Collections.emptyList(), vulnerabilities,
                Collections.emptyList());
        notifications.add(vulnerability);
        final Collection<ProjectData> projectList = processor.process(notifications);

        for (final ProjectData projectData : projectList) {
            assertEquals(3, projectData.getCategoryMap().size());
            for (final CategoryData category : projectData.getCategoryMap().values()) {
                for (final ItemData itemData : category.getItemList()) {
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));
                }
            }
        }
    }

    @Test
    public void testVulnerabilityDeleted() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final DateTime dateTime = new DateTime();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, Collections.emptyList(),
                Collections.emptyList(),
                vulnerabilities);
        notifications.add(vulnerability);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertTrue(projectList.isEmpty());
    }

    @Test
    public void testVulnAddedAndDeleted() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final DateTime dateTime = new DateTime();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, vulnerabilities, Collections.emptyList(),
                vulnerabilities);
        notifications.add(vulnerability);
        final Collection<ProjectData> projectList = processor.process(notifications);
        assertTrue(projectList.isEmpty());
    }

    @Test
    public void testComplexVulnerability() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();

        final List<VulnerabilitySourceQualifiedId> resultVulnList = new ArrayList<>(2);
        resultVulnList.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        resultVulnList.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(resultVulnList);
        initVulnerabilityTest(vulnerabilityList);

        final List<VulnerabilitySourceQualifiedId> added = new ArrayList<>(3);
        added.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated = new ArrayList<>(4);
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted = new ArrayList<>(3);

        deleted.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, added, updated, deleted);
        notifications.add(vulnerability);

        final Collection<ProjectData> projectList = processor.process(notifications);
        assertFalse(projectList.isEmpty());
        final Map<String, Integer> categoryItemMap = new HashMap<>();
        for (final ProjectData projectData : projectList) {
            assertEquals(2, projectData.getCategoryMap().size());
            for (final CategoryData category : projectData.getCategoryMap().values()) {
                categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
                for (final ItemData itemData : category.getItemList()) {
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));
                }
            }
        }
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
    }

    @Test
    public void testComplexVulnerabilityMulti() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();

        final List<VulnerabilitySourceQualifiedId> resultVulnList = new ArrayList<>(2);
        resultVulnList.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        resultVulnList.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        List<VulnerabilityItem> vulnerabilityList = testUtil.createVulnerabiltyItemList(resultVulnList);
        initVulnerabilityTest(vulnerabilityList);

        final List<VulnerabilitySourceQualifiedId> added1 = new LinkedList<>();
        added1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated1 = new LinkedList<>();
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted1 = new LinkedList<>();

        deleted1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, added1, updated1, deleted1);
        notifications.add(vulnerability);

        final List<VulnerabilitySourceQualifiedId> added2 = new LinkedList<>();
        added1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated2 = new LinkedList<>();
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted2 = new LinkedList<>();

        deleted1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted1.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability2 = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, added2, updated2, deleted2);
        notifications.add(vulnerability2);

        final Collection<ProjectData> projectList = processor.process(notifications);
        assertFalse(projectList.isEmpty());
        final Map<String, Integer> categoryItemMap = new HashMap<>();
        for (final ProjectData projectData : projectList) {
            assertEquals(2, projectData.getCategoryMap().size());
            for (final CategoryData category : projectData.getCategoryMap().values()) {
                categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
                for (final ItemData itemData : category.getItemList()) {
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));
                }
            }
        }
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
    }

    // @Test
    public void testComplexAllTypes() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyOverride = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyOverride);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyCleared);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);

        final List<VulnerabilitySourceQualifiedId> added = new LinkedList<>();
        added.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated = new LinkedList<>();
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted = new LinkedList<>();

        deleted.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted.add(new VulnerabilitySourceQualifiedId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION, added, updated, deleted);
        notifications.add(vulnerability);

        final Collection<ProjectData> projectList = processor.process(notifications);
        assertFalse(projectList.isEmpty());
        final Map<String, Integer> categoryItemMap = new HashMap<>();
        for (final ProjectData projectData : projectList) {
            assertEquals(3, projectData.getCategoryMap().size());
            for (final CategoryData category : projectData.getCategoryMap().values()) {
                categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
                for (final ItemData itemData : category.getItemList()) {
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));
                }
            }
        }
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.POLICY_VIOLATION.name()).intValue());
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
        assertEquals(2, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
    }

    public void testMultiProjectPolicy() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT2,
                ProcessorTestUtil.VERSION2);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME2, ProcessorTestUtil.PROJECT_VERSION_NAME2,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME2, ProcessorTestUtil.PROJECT_VERSION_NAME2,
                ProcessorTestUtil.COMPONENT2,
                ProcessorTestUtil.VERSION2);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyOverride = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, ProcessorTestUtil.VERSION);
        notifications.add(policyOverride);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                ProcessorTestUtil.VERSION);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT2, ProcessorTestUtil.VERSION);
        notifications.add(policyCleared);
        policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT2,
                ProcessorTestUtil.VERSION2);
        notifications.add(policyCleared);

        final Collection<ProjectData> projectList = processor.process(notifications);
        assertFalse(projectList.isEmpty());
        final Map<String, Integer> categoryItemMap = new HashMap<>();
        for (final ProjectData projectData : projectList) {
            assertEquals(3, projectData.getCategoryMap().size());
            for (final CategoryData category : projectData.getCategoryMap().values()) {
                categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
                for (final ItemData itemData : category.getItemList()) {
                    final Set<ItemEntry> dataSet = itemData.getDataSet();
                    final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT);
                    assertTrue(dataSet.contains(componentKey));

                    final ItemEntry versionKey = new ItemEntry("", ProcessorTestUtil.VERSION);
                    assertTrue(dataSet.contains(versionKey));
                    final ItemEntry componentKey2 = new ItemEntry(ItemTypeEnum.COMPONENT.name(), ProcessorTestUtil.COMPONENT2);
                    assertTrue(dataSet.contains(componentKey2));

                    final ItemEntry versionKey2 = new ItemEntry("", ProcessorTestUtil.VERSION2);
                    assertTrue(dataSet.contains(versionKey2));
                }
            }
        }
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.POLICY_VIOLATION.name()).intValue());
        assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
        assertEquals(2, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
    }
}
