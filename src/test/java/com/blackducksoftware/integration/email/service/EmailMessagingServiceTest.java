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
package com.blackducksoftware.integration.email.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.EmailEngine;
import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;
import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.mock.TestEmailEngine;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.notifier.AbstractDigestNotifier;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {

    private EmailEngine engine;

    @Before
    public void init() throws Exception {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL propFileUrl = classLoader.getResource("extension.properties");
        final File file = new File(propFileUrl.toURI());
        System.setProperty(ExtensionConfigManager.PROPERTY_KEY_CONFIG_LOCATION_PATH, file.getCanonicalFile().getParent());
        engine = new TestEmailEngine();
        engine.start();
    }

    @Test
    public void testSendingEmail() throws IOException, MessagingException, TemplateException {
        final Map<String, Object> model = new HashMap<>();
        model.put("title", "A Glorious Day");
        model.put("message", "this should have html and plain text parts");
        model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));
        final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "sampleTemplate.ftl", model);
        engine.getEmailMessagingService().sendEmailMessage(target);
    }

    @Test
    public void testDigest() throws Exception {
        final List<ProjectData> projectDataList = createProjectData();
        final Map<String, Object> model = new HashMap<>();
        model.put(AbstractDigestNotifier.KEY_START_DATE, String.valueOf(new Date()));
        model.put(AbstractDigestNotifier.KEY_END_DATE, String.valueOf(new Date()));
        model.put(AbstractDigestNotifier.KEY_USER_FIRST_NAME, "Hub");
        model.put(AbstractDigestNotifier.KEY_USER_LAST_NAME, "User");
        model.put(AbstractDigestNotifier.KEY_TOPICS_LIST, projectDataList);
        model.put("hub_server_url", "http://hub-a.domain.com1/");
        model.put(AbstractDigestNotifier.KEY_NOTIFIER_CATEGORY, "Daily");

        final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "digest.ftl", model);
        engine.getEmailMessagingService().sendEmailMessage(target);
    }

    private List<ProjectData> createProjectData() {
        final List<ProjectData> filteredList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final List<ItemData> itemList = new ArrayList<>(15);
            for (int itemIndex = 0; itemIndex < 15; itemIndex++) {
                final Set<ItemEntry> dataSet = new HashSet<>();
                dataSet.add(new ItemEntry("KEY_" + itemIndex, "VALUE_" + itemIndex));
                itemList.add(new ItemData(dataSet));
            }
            final String projectName = "PROJECT_NAME";
            final String projectVersion = "PROJECT_VERSION";
            final Map<NotificationCategoryEnum, CategoryData> categoryMap = new HashMap<>();
            for (final NotificationCategoryEnum category : NotificationCategoryEnum.values()) {
                categoryMap.put(category, new CategoryData(category.name(), itemList, 1));
            }
            filteredList.add(new ProjectData(projectName, projectVersion, categoryMap));
        }

        return filteredList;
    }
}
