package com.blackducksoftware.integration.email.notifier;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailExtensionConstants;
import com.blackducksoftware.integration.email.ExtensionLogger;
import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;
import com.blackducksoftware.integration.email.batch.processor.NotificationProcessor;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.extension.ConfigurationItem;
import com.blackducksoftware.integration.hub.api.user.UserItem;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.extension.item.UserConfigItem;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;

public abstract class AbstractDigestNotifier extends AbstractNotifier {
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

    private final String cronExpression;

    private final NotificationDataService notificationDataService;

    private final ExtensionConfigDataService extensionConfigDataService;

    public AbstractDigestNotifier(final ExtensionProperties customerProperties,
            final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory) {
        super(customerProperties, emailMessagingService, dataServicesFactory);

        final String quartzTriggerPropValue = getCustomerProperties().getNotifierVariableProperties()
                .get(getNotifierPropertyKey() + ".cron.expression");
        cronExpression = StringUtils.trimToNull(quartzTriggerPropValue);
        final Logger logger = LoggerFactory.getLogger(NotificationDataService.class);
        final ExtensionLogger extLogger = new ExtensionLogger(logger);
        notificationDataService = dataServicesFactory.createNotificationDataService(extLogger);
        extensionConfigDataService = dataServicesFactory.createExtensionConfigDataService(extLogger);
    }

    public abstract DateRange createDateRange(final ZoneId zone);

    public abstract String getCategory();

    @Override
    public void run() {
        try {
            logger.info("Starting iteration of {} digest email notifier", getName());
            final ExtensionProperties globalConfig = createPropertiesFromGlobalConfig();
            final List<UserConfigItem> userConfigList = extensionConfigDataService
                    .getUserConfigList(getHubExtensionUri());
            final List<UserConfigItem> usersInCategory = createUserListInCategory(userConfigList);

            if (usersInCategory.isEmpty()) {
                logger.info("No Users opted into this email notification");
            } else {
                final ZoneId zoneId = getZoneId(globalConfig);
                final DateRange dateRange = createDateRange(zoneId);
                final Date startDate = dateRange.getStart();
                final Date endDate = dateRange.getEnd();
                logger.info("Getting notification data between start: {} end: {}", startDate, endDate);
                logger.info("Number of users opted into this email template {}", usersInCategory.size());
                int filteredUsers = 0;
                for (final UserConfigItem userConfig : usersInCategory) {
                    try {
                        UserItem userItem = userConfig.getUser();
                        logger.info("Processing hub user {}", userItem.getMeta().getHref());
                        final SortedSet<NotificationContentItem> notifications = notificationDataService.getUserNotifications(startDate, endDate,
                                userItem);
                        final NotificationProcessor processor = new NotificationProcessor(getDataServicesFactory());
                        final Collection<ProjectData> projectList = processor.process(notifications);
                        if (projectList.isEmpty()) {
                            logger.info("Project Aggregated Data list is empty no email to generate");
                        } else {
                            // TODO use the data services factory to execute
                            // this code in the for loop in a separate thread
                            // sending emails for each user can be done
                            // independently of one another at this point.
                            // TODO need to filter out on the user's project
                            // filter chain pattern make sense?
                            Collection<ProjectData> projectsDigest = filterUserProjects(projectList, userConfig);
                            projectsDigest = filterCategories(projectList, userConfig);
                            if (projectsDigest.isEmpty()) {
                                filteredUsers++;
                            } else {
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

    private ZoneId getZoneId(final ExtensionProperties globalConfig) {
        final ZoneId defaultZoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC);

        final String timezoneVarKey = "all.timezone";
        final Map<String, String> notifierVariableMap = globalConfig.getNotifierVariableProperties();
        if (!notifierVariableMap.containsKey(timezoneVarKey)) {
            return defaultZoneId;
        } else {
            final String timezoneId = notifierVariableMap.get(timezoneVarKey);
            if (StringUtils.isBlank(timezoneId)) {
                return defaultZoneId;
            } else {
                try {
                    return ZoneId.of(timezoneId);
                } catch (final DateTimeException ex) {
                    logger.error("Error parsing global config timezone. Using UTC timezone", ex);
                    return defaultZoneId;
                }
            }
        }
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

    private ExtensionProperties createPropertiesFromGlobalConfig() throws UnexpectedHubResponseException {
        final Map<String, ConfigurationItem> globalMap = extensionConfigDataService
                .getGlobalConfigMap(getHubExtensionUri());
        final Properties globalProperties = new Properties();
        for (final Map.Entry<String, ConfigurationItem> entry : globalMap.entrySet()) {
            globalProperties.put(entry.getKey(), entry.getValue().getValue().get(0));
        }
        return new ExtensionProperties(globalProperties);
    }

    @Override
    public String getTemplateName() {
        return "digest.ftl";
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }
}
