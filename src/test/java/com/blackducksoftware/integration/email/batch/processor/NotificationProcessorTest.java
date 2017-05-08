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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.email.mock.MockLogger;
import com.blackducksoftware.integration.email.mock.MockRestConnection;
import com.blackducksoftware.integration.hub.api.item.MetaService;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityRequestService;
import com.blackducksoftware.integration.hub.dataservice.notification.model.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.model.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.model.view.ComponentVersionView;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.model.view.components.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.notification.processor.ItemTypeEnum;
import com.blackducksoftware.integration.hub.notification.processor.NotificationCategoryEnum;
import com.blackducksoftware.integration.hub.notification.processor.event.NotificationEvent;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubResponseService;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.log.IntLogger;
import com.google.gson.Gson;

public class NotificationProcessorTest {

    private final ProcessorTestUtil testUtil = new ProcessorTestUtil();

    private MetaService metaService;

    private Gson gson;

    @Before
    public void init() throws Exception {
        final RestConnection restConnection = new MockRestConnection(new MockLogger(), null);
        final HubServicesFactory factory = new HubServicesFactory(restConnection);
        final IntLogger logger = new IntBufferedLogger();
        metaService = factory.createMetaService(logger);
        gson = restConnection.gson;
    }

    public MockProcessor createMockedNotificationProcessor() {
        final VulnerabilityRequestService vulnerabilityRequestService = Mockito.mock(VulnerabilityRequestService.class);
        final HubResponseService hubResponseService = Mockito.mock(HubResponseService.class);
        final MockProcessor processor = new MockProcessor(hubResponseService, vulnerabilityRequestService, metaService);
        return processor;
    }

    public MockProcessor createMockedNotificationProcessor(final List<VulnerabilityView> vulnerabilityList) throws Exception {
        final ComponentVersionView compVersion = Mockito.mock(ComponentVersionView.class);
        compVersion.json = createComponentJson();
        final VulnerabilityRequestService vulnerabilityRequestService = Mockito.mock(VulnerabilityRequestService.class);
        final HubResponseService hubResponseService = Mockito.mock(HubResponseService.class);
        Mockito.when(hubResponseService.getItem(Mockito.anyString(), Mockito.eq(ComponentVersionView.class))).thenReturn(compVersion);
        Mockito.when(vulnerabilityRequestService.getComponentVersionVulnerabilities(Mockito.anyString())).thenReturn(vulnerabilityList);
        final MockProcessor processor = new MockProcessor(hubResponseService, vulnerabilityRequestService, metaService);
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

    private void assertPolicyDataValid(final Collection<NotificationEvent> eventList, final NotificationCategoryEnum categoryType) {
        int ruleIndex = 1;
        for (final NotificationEvent event : eventList) {
            final NotificationContentItem content = (NotificationContentItem) event.getDataSet().get(NotificationEvent.DATA_SET_KEY_NOTIFICATION_CONTENT);
            assertEquals(ProcessorTestUtil.PROJECT_NAME, content.getProjectVersion().getProjectName());
            assertEquals(ProcessorTestUtil.PROJECT_VERSION_NAME, content.getProjectVersion().getProjectVersionName());
            final Map<String, Object> dataSet = event.getDataSet();
            assertEquals(ProcessorTestUtil.COMPONENT, dataSet.get(ItemTypeEnum.COMPONENT.name()));
            assertEquals(ProcessorTestUtil.VERSION, dataSet.get(ItemTypeEnum.VERSION.name()));
            assertEquals(ProcessorTestUtil.PREFIX_RULE + ruleIndex, dataSet.get(ItemTypeEnum.RULE.name()));
            ruleIndex++;
        }
    }

    @Test
    public void testPolicyViolationAdd() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();

        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        notifications.add(
                testUtil.createPolicyViolation(new Date(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT,
                        componentVersion));
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);

        assertPolicyDataValid(eventList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testPolicyViolationOverride() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        notifications.add(
                testUtil.createPolicyOverride(new Date(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT,
                        componentVersion));
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertFalse(eventList.isEmpty());
        assertPolicyDataValid(eventList, NotificationCategoryEnum.POLICY_VIOLATION_OVERRIDE);
    }

    @Test
    public void testPolicyViolationCleared() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        notifications.add(
                testUtil.createPolicyCleared(new Date(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT,
                        componentVersion));
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertFalse(eventList.isEmpty());
        assertPolicyDataValid(eventList, NotificationCategoryEnum.POLICY_VIOLATION_CLEARED);
    }

    @Test
    public void testPolicyViolationAndOverride() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        DateTime dateTime = new DateTime();
        final PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyOverride = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyOverride);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertTrue(eventList.isEmpty());
    }

    @Test
    public void testPolicyViolationAndCleared() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        DateTime dateTime = new DateTime();
        final PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyCleared);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertTrue(eventList.isEmpty());
    }

    @Test
    public void testPolicyViolationAndClearedAndViolated() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);

        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyCleared);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                componentVersion);
        notifications.add(policyViolation);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertPolicyDataValid(eventList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testPolicyViolationAndOverrideAndViolated() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyCleared = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyCleared);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                componentVersion);
        notifications.add(policyViolation);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertPolicyDataValid(eventList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testComplexPolicyOverride() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        PolicyViolationContentItem policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyOverrideContentItem policyOverride = testUtil.createPolicyOverride(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyOverride);
        dateTime = dateTime.plusSeconds(1);
        policyViolation = testUtil.createPolicyViolation(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME, ProcessorTestUtil.PROJECT_VERSION_NAME,
                ProcessorTestUtil.COMPONENT,
                componentVersion);
        notifications.add(policyViolation);
        dateTime = dateTime.plusSeconds(1);
        final PolicyViolationClearedContentItem policyCleared = testUtil.createPolicyCleared(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion);
        notifications.add(policyCleared);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertPolicyDataValid(eventList, NotificationCategoryEnum.POLICY_VIOLATION);
    }

    @Test
    public void testVulnerabilityAdded() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        final List<VulnerabilityView> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilities, gson);
        final DateTime dateTime = new DateTime();
        final List<VulnerabilitySourceQualifiedId> emptyVulnSourceList = Collections.emptyList();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, vulnerabilities, emptyVulnSourceList,
                emptyVulnSourceList);
        notifications.add(vulnerability);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor(vulnerabilityList).process(notifications);

        for (final NotificationEvent event : eventList) {
            final Map<String, Object> dataSet = event.getDataSet();
            assertEquals(ProcessorTestUtil.COMPONENT, dataSet.get(ItemTypeEnum.COMPONENT.name()));
            assertEquals(ProcessorTestUtil.VERSION, dataSet.get(ItemTypeEnum.VERSION.name()));
        }
    }

    @Test
    public void testVulnerabilityUpdated() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        final List<VulnerabilityView> vulnerabilityList = testUtil.createVulnerabiltyItemList(vulnerabilities, gson);

        final DateTime dateTime = new DateTime();
        final List<VulnerabilitySourceQualifiedId> emptyVulnSourceList = Collections.emptyList();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, emptyVulnSourceList, vulnerabilities,
                emptyVulnSourceList);
        notifications.add(vulnerability);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor(vulnerabilityList).process(notifications);

        for (final NotificationEvent event : eventList) {
            final Map<String, Object> dataSet = event.getDataSet();
            assertEquals(ProcessorTestUtil.COMPONENT, dataSet.get(ItemTypeEnum.COMPONENT.name()));
            assertEquals(ProcessorTestUtil.VERSION, dataSet.get(ItemTypeEnum.VERSION.name()));
        }
    }

    @Test
    public void testVulnerabilityDeleted() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final DateTime dateTime = new DateTime();
        final List<VulnerabilitySourceQualifiedId> emptyVulnSourceList = Collections.emptyList();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, emptyVulnSourceList,
                emptyVulnSourceList,
                vulnerabilities);
        notifications.add(vulnerability);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertTrue(eventList.isEmpty());
    }

    @Test
    public void testVulnAddedAndDeleted() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        vulnerabilities.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final DateTime dateTime = new DateTime();
        final List<VulnerabilitySourceQualifiedId> emptyVulnSourceList = Collections.emptyList();
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, vulnerabilities, emptyVulnSourceList,
                vulnerabilities);
        notifications.add(vulnerability);
        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor().process(notifications);
        assertTrue(eventList.isEmpty());
    }

    @Test
    public void testComplexVulnerability() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        final List<VulnerabilitySourceQualifiedId> resultVulnList = new ArrayList<>(2);
        resultVulnList.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        resultVulnList.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        final List<VulnerabilityView> vulnerabilityList = testUtil.createVulnerabiltyItemList(resultVulnList, gson);

        final List<VulnerabilitySourceQualifiedId> added = new ArrayList<>(3);
        added.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated = new ArrayList<>(4);
        updated.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted = new ArrayList<>(3);

        deleted.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, added, updated, deleted);
        notifications.add(vulnerability);

        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor(vulnerabilityList).process(notifications);
        assertFalse(eventList.isEmpty());
        for (final NotificationEvent event : eventList) {

            final Map<String, Object> dataSet = event.getDataSet();
            assertEquals(ProcessorTestUtil.COMPONENT, dataSet.get(ItemTypeEnum.COMPONENT.name()));
            assertEquals(ProcessorTestUtil.VERSION, dataSet.get(ItemTypeEnum.VERSION.name()));

        }
    }

    @Test
    public void testComplexVulnerabilityMulti() throws Exception {
        final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
        DateTime dateTime = new DateTime();
        final ComponentVersionView componentVersion = Mockito.mock(ComponentVersionView.class);
        Mockito.when(componentVersion.getVersionName()).thenReturn(ProcessorTestUtil.VERSION);
        final List<VulnerabilitySourceQualifiedId> resultVulnList = new ArrayList<>(2);
        resultVulnList.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        resultVulnList.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        final List<VulnerabilityView> vulnerabilityList = testUtil.createVulnerabiltyItemList(resultVulnList, gson);

        final List<VulnerabilitySourceQualifiedId> added1 = new LinkedList<>();
        added1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated1 = new LinkedList<>();
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted1 = new LinkedList<>();

        deleted1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, added1, updated1, deleted1);
        notifications.add(vulnerability);

        final List<VulnerabilitySourceQualifiedId> added2 = new LinkedList<>();
        added1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        added1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID));
        added1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));

        final List<VulnerabilitySourceQualifiedId> updated2 = new LinkedList<>();
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.HIGH_VULN_ID));
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.MEDIUM_VULN_ID2));
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        updated1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));

        final List<VulnerabilitySourceQualifiedId> deleted2 = new LinkedList<>();

        deleted1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID));
        deleted1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID2));
        deleted1.add(testUtil.createVulnerabilitySourceId(ProcessorTestUtil.VULN_SOURCE, ProcessorTestUtil.LOW_VULN_ID1));
        dateTime = dateTime.plusSeconds(1);
        final VulnerabilityContentItem vulnerability2 = testUtil.createVulnerability(dateTime.toDate(), ProcessorTestUtil.PROJECT_NAME,
                ProcessorTestUtil.PROJECT_VERSION_NAME, ProcessorTestUtil.COMPONENT, componentVersion, added2, updated2, deleted2);
        notifications.add(vulnerability2);

        final Collection<NotificationEvent> eventList = createMockedNotificationProcessor(vulnerabilityList).process(notifications);
        assertFalse(eventList.isEmpty());
        for (final NotificationEvent event : eventList) {
            final Map<String, Object> dataSet = event.getDataSet();
            assertEquals(ProcessorTestUtil.COMPONENT, dataSet.get(ItemTypeEnum.COMPONENT.name()));
            assertEquals(ProcessorTestUtil.VERSION, dataSet.get(ItemTypeEnum.VERSION.name()));
        }
    }
}
