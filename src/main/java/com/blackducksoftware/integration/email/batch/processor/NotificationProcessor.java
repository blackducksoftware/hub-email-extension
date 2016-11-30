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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.batch.CategoryDataBuilder;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.model.batch.ProjectDataBuilder;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityRequestService;
import com.blackducksoftware.integration.hub.dataservice.notification.item.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservice.notification.item.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.service.HubRequestService;

public class NotificationProcessor {
    private final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);

    private final Map<Class<?>, NotificationSubProcessor<?>> processorMap = new HashMap<>();

    private final List<SubProcessorCache<?>> cacheList = new ArrayList<>();

    public NotificationProcessor(HubRequestService hubRequestService, VulnerabilityRequestService vulnerabilityRequestService) {
        final SubProcessorCache<PolicyEvent> policyCache = new SubProcessorCache<>();
        final VulnerabilityCache vulnerabilityCache = new VulnerabilityCache(hubRequestService, vulnerabilityRequestService);
        cacheList.add(policyCache);
        cacheList.add(vulnerabilityCache);
        processorMap.put(PolicyViolationContentItem.class, new PolicyViolationProcessor(policyCache));
        processorMap.put(PolicyViolationClearedContentItem.class, new PolicyViolationClearedProcessor(policyCache));
        processorMap.put(PolicyOverrideContentItem.class, new PolicyOverrideProcessor(policyCache));
        processorMap.put(VulnerabilityContentItem.class,
                new VulnerabilityProcessor(vulnerabilityCache));

    }

    public Collection<ProjectData> process(final SortedSet<NotificationContentItem> notifications) {
        createEvents(notifications);
        final Collection<ProjectData> projectDataList = processEvents();
        return projectDataList;
    }

    private void createEvents(final SortedSet<NotificationContentItem> notifications) {
        for (final NotificationContentItem item : notifications) {
            final Class<?> key = item.getClass();
            if (!processorMap.containsKey(key)) {
                logger.error("Could not find converter for notification: {}", item);
            } else {
                final NotificationSubProcessor<?> processor = processorMap.get(key);
                processor.process(item);
            }
        }
    }

    private Collection<ProjectData> processEvents() {
        final Collection<NotificationEvent<?>> eventMap = processNotificationEvents();
        final Collection<ProjectData> projectMap = createCateoryDataMap(eventMap);
        return projectMap;
    }

    private Collection<NotificationEvent<?>> processNotificationEvents() {
        final Collection<NotificationEvent<?>> eventList = new LinkedList<>();
        for (final SubProcessorCache<?> processor : cacheList) {
            eventList.addAll(processor.getEvents());
        }
        return eventList;
    }

    private Collection<ProjectData> createCateoryDataMap(final Collection<NotificationEvent<?>> eventMap) {
        final Map<String, ProjectDataBuilder> projectDataMap = new LinkedHashMap<>();
        for (final NotificationEvent<?> entry : eventMap) {
            final NotificationEvent<?> event = entry;
            final String projectKey = event.getNotificationContent().getProjectVersion().getUrl();
            // get category map from the project or create the project data if
            // it doesn't exist
            Map<NotificationCategoryEnum, CategoryDataBuilder> categoryBuilderMap;
            if (!projectDataMap.containsKey(projectKey)) {
                final ProjectDataBuilder projectBuilder = new ProjectDataBuilder();
                projectBuilder.setProjectName(event.getNotificationContent().getProjectVersion().getProjectName());
                projectBuilder.setProjectVersion(event.getNotificationContent().getProjectVersion().getProjectVersionName());
                projectDataMap.put(projectKey, projectBuilder);
                categoryBuilderMap = projectBuilder.getCategoryBuilderMap();
            } else {
                final ProjectDataBuilder projectBuilder = projectDataMap.get(projectKey);
                categoryBuilderMap = projectBuilder.getCategoryBuilderMap();
            }
            // get the category data object to be able to add items.
            CategoryDataBuilder categoryData;
            final NotificationCategoryEnum categoryKey = event.getCategoryType();
            if (!categoryBuilderMap.containsKey(categoryKey)) {
                categoryData = new CategoryDataBuilder();
                categoryData.setCategoryKey(categoryKey.name());
                categoryBuilderMap.put(categoryKey, categoryData);
            } else {
                categoryData = categoryBuilderMap.get(categoryKey);
            }
            categoryData.incrementItemCount(event.countCategoryItems());
            categoryData.addItem(new ItemData(new LinkedHashSet<>(event.getDataSet())));
        }
        // build
        final Collection<ProjectData> dataList = new LinkedList<>();
        for (final ProjectDataBuilder builder : projectDataMap.values()) {
            dataList.add(builder.build());
        }
        return dataList;
    }
}
