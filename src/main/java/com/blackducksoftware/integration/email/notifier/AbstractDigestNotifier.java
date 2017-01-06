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
package com.blackducksoftware.integration.email.notifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailExtensionConstants;
import com.blackducksoftware.integration.email.ExtensionLogger;
import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;
import com.blackducksoftware.integration.email.batch.processor.NotificationProcessor;
import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.HubIntRestService;
import com.blackducksoftware.integration.hub.api.user.UserItem;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.item.UserConfigItem;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.ResourceDoesNotExistException;
import com.blackducksoftware.integration.phone.home.PhoneHomeClient;
import com.blackducksoftware.integration.phone.home.exception.PhoneHomeException;
import com.blackducksoftware.integration.phone.home.exception.PropertiesLoaderException;

public abstract class AbstractDigestNotifier extends IntervalNotifier {
    private static final String KEY_HUB_SERVER_URL = "hub_server_url";

    public static final String KEY_TOPICS_LIST = "topicsList";

    public static final String KEY_START_DATE = "startDate";

    public static final String KEY_END_DATE = "endDate";

    public static final String KEY_TOTAL_NOTIFICATIONS = "totalNotifications";

    public static final String KEY_TOTAL_POLICY_VIOLATIONS = "totalPolicyViolations";

    public static final String KEY_TOTAL_POLICY_OVERRIDES = "totalPolicyOverrides";

    public static final String KEY_TOTAL_VULNERABILITIES = "totalVulnerabilities";

    public static final String KEY_NOTIFIER_CATEGORY = "emailCategory";

    public static final String KEY_USER_FIRST_NAME = "user_first_name";

    public static final String KEY_USER_LAST_NAME = "user_last_name";

    private final Logger logger = LoggerFactory.getLogger(AbstractDigestNotifier.class);

    private final NotificationDataService notificationDataService;

    public AbstractDigestNotifier(final ExtensionProperties extensionProperties,
            final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory, final ExtensionInfo extensionInfoData) {
        super(extensionProperties, emailMessagingService, dataServicesFactory, extensionInfoData);
        final Logger logger = LoggerFactory.getLogger(NotificationDataService.class);
        final ExtensionLogger extLogger = new ExtensionLogger(logger);
        notificationDataService = dataServicesFactory.createNotificationDataService(extLogger);
    }

    public abstract String getCategory();

    @Override
    public void run() {
        try {
            logger.info("Starting iteration of {} digest email notifier", getName());
            final ExtensionProperties globalConfig = createPropertiesFromGlobalConfig();
            final List<UserConfigItem> userConfigList = getExtensionConfigDataService()
                    .getUserConfigList(getHubExtensionUri());
            final List<UserConfigItem> usersInCategory = createUserListInCategory(userConfigList);

            if (usersInCategory.isEmpty()) {
                logger.info("No Users opted into this email notification");
            } else {
                final DateRange dateRange = createDateRange();
                final Date startDate = dateRange.getStart();
                final Date endDate = dateRange.getEnd();
                logger.info("Getting notification data between start: {} end: {}", startDate, endDate);
                logger.info("Number of users opted into this email template {}", usersInCategory.size());
                int filteredUsers = 0;
                for (final UserConfigItem userConfig : usersInCategory) {
                    try {
                        final UserItem userItem = userConfig.getUser();
                        logger.info("Processing hub user {}", userItem.getMeta().getHref());
                        final SortedSet<NotificationContentItem> notifications = notificationDataService.getUserNotifications(startDate, endDate,
                                userItem);
                        final NotificationProcessor processor = new NotificationProcessor(getDataServicesFactory());
                        final Collection<ProjectData> projectList = processor.process(notifications);
                        if (projectList.isEmpty()) {
                            logger.info("Project Aggregated Data list is empty no email to generate");
                        } else {
                            // TODO need to filter out on the user's project
                            // filter chain pattern make sense?
                            Collection<ProjectData> projectsDigest = filterUserProjects(projectList, userConfig);
                            projectsDigest = filterCategories(projectList, userConfig);
                            if (projectsDigest.isEmpty()) {
                                filteredUsers++;
                            } else {
                                bdPhoneHome(); // extension used.
                                final Map<String, Object> model = new HashMap<>();
                                model.put(KEY_TOPICS_LIST, projectsDigest);
                                model.put(KEY_START_DATE, String.valueOf(startDate));
                                model.put(KEY_END_DATE, String.valueOf(endDate));
                                model.put(KEY_USER_FIRST_NAME, userConfig.getUser().getFirstName());
                                model.put(KEY_USER_LAST_NAME, userConfig.getUser().getLastName());
                                model.put(KEY_NOTIFIER_CATEGORY, getCategory().toUpperCase());
                                model.put(KEY_HUB_SERVER_URL,
                                        getDataServicesFactory().getRestConnection().getBaseUrl());
                                final String emailAddress = userConfig.getUser().getEmail();
                                final String templateName = getTemplateName(userConfig);
                                final EmailTarget emailTarget = new EmailTarget(emailAddress, templateName, model);
                                getEmailMessagingService().sendEmailMessage(emailTarget, globalConfig);
                            }
                        }

                    } catch (final Exception e) {
                        logger.error("Error sending email to user", e);
                    }
                }
                logger.info("Number of users filtered out of email template: {}", filteredUsers);
            }
        } catch (final Exception e) {
            logger.error("Error sending the email", e);
        }
        logger.info("Finished iteration of {} digest email notifier",

                getName());
    }

    private Collection<ProjectData> filterUserProjects(final Collection<ProjectData> projectList,
            final UserConfigItem userConfig) {
        final List<ProjectData> filteredList = new ArrayList<>(projectList.size());

        return filteredList;
    }

    private Collection<ProjectData> filterCategories(final Collection<ProjectData> projectList,
            final UserConfigItem userConfig) {
        final List<ProjectData> filteredList = new ArrayList<>(projectList.size());
        final Set<NotificationCategoryEnum> triggerSet = getTriggerSet(userConfig);

        if (!triggerSet.isEmpty()) {
            for (final ProjectData projectData : projectList) {
                final Map<NotificationCategoryEnum, CategoryData> categoryDataMap = new TreeMap<>();
                final ProjectData newProject = new ProjectData(projectData.getProjectName(),
                        projectData.getProjectVersion(), categoryDataMap);
                for (final Map.Entry<NotificationCategoryEnum, CategoryData> entry : projectData.getCategoryMap()
                        .entrySet()) {
                    if (triggerSet.contains(entry.getKey())) {
                        categoryDataMap.put(entry.getKey(), entry.getValue());
                    }
                }

                if (!categoryDataMap.isEmpty()) {
                    filteredList.add(newProject);
                }
            }
        }

        return filteredList;
    }

    private List<UserConfigItem> createUserListInCategory(final List<UserConfigItem> userConfigList) {
        final List<UserConfigItem> itemList = new ArrayList<>(userConfigList.size());

        for (final UserConfigItem userConfig : userConfigList) {
            final boolean optedIn = isOptedIn(userConfig);
            final boolean categoryMatch = doesCategoryMatch(userConfig);
            if (optedIn && categoryMatch) {
                itemList.add(userConfig);
            }
        }

        return itemList;
    }

    private boolean isOptedIn(final UserConfigItem userConfig) {
        final String value = getSingleConfigValue(userConfig, EmailExtensionConstants.CONFIG_KEY_OPT_IN);
        return Boolean.parseBoolean(value);
    }

    private String getTemplateName(final UserConfigItem userConfig) {
        final String templateName = getSingleConfigValue(userConfig, EmailExtensionConstants.CONFIG_KEY_TEMPLATE_NAME);
        if (StringUtils.isNotBlank(templateName)) {
            return templateName;
        } else {
            return getTemplateName();
        }
    }

    private boolean doesCategoryMatch(final UserConfigItem userConfig) {
        final String emailFrequency = getSingleConfigValue(userConfig, EmailExtensionConstants.CONFIG_KEY_FREQUENCY);
        return getCategory().equals(emailFrequency);
    }

    private Set<NotificationCategoryEnum> getTriggerSet(final UserConfigItem userConfig) {
        final List<String> triggerList = getConfigValueList(userConfig, EmailExtensionConstants.CONFIG_KEY_TRIGGERS);
        final Set<NotificationCategoryEnum> triggerSet = new HashSet<>();
        for (final String trigger : triggerList) {
            try {
                triggerSet.add(NotificationCategoryEnum.valueOf(trigger));
            } catch (final Exception ex) {
                logger.error("Could not parse trigger config {} {}", trigger, ex);
            }
        }
        return triggerSet;
    }

    private String getSingleConfigValue(final UserConfigItem userConfig, final String key) {
        if (userConfig.getConfigMap().containsKey(key)) {
            final String value = userConfig.getConfigMap().get(key).getValue().get(0);
            return value;
        } else {
            return "";
        }
    }

    private List<String> getConfigValueList(final UserConfigItem userConfig, final String key) {
        if (userConfig.getConfigMap().containsKey(key)) {
            final List<String> value = userConfig.getConfigMap().get(key).getValue();
            return value;
        } else {
            return new ArrayList<>();
        }
    }

    private void bdPhoneHome() {
        try {
            final HubIntRestService intRestService = new HubIntRestService(getDataServicesFactory().getRestConnection());
            final String hubVersion = getDataServicesFactory().getHubVersionRestService().getHubVersion();
            String regId = null;
            String hubHostName = null;
            try {
                regId = intRestService.getRegistrationId();
            } catch (final Exception e) {
                logger.debug("Could not get the Hub registration Id.");
            }
            try {
                final URL url = new URL(getDataServicesFactory().getRestConnection().getBaseUrl());
                hubHostName = url.getHost();
            } catch (final Exception e) {
                logger.debug("Could not get the Hub Host name.");
            }

            final String pluginVersion = getExtensionProperties().getExtensionVersion();
            final PhoneHomeClient phClient = new PhoneHomeClient();

            phClient.callHomeIntegrations(regId, hubHostName, "Hub", hubVersion, "email-extension", "",
                    pluginVersion);
        } catch (ResourceException | JSONException | IOException | PropertiesLoaderException | PhoneHomeException | URISyntaxException
                | ResourceDoesNotExistException | BDRestException ex) {
            logger.debug("Unable to phone-home", ex);
        }
    }

    @Override
    public String getTemplateName() {
        return "digest.ftl";
    }
}
