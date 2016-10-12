package com.blackducksoftware.integration.email.notifier;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailExtensionConstants;
import com.blackducksoftware.integration.email.batch.processor.NotificationProcessor;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.extension.ConfigurationItem;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.extension.item.UserConfigItem;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;

public abstract class AbstractDigestNotifier extends AbstractNotifier {
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

	public AbstractDigestNotifier(final ExtensionProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory) {
		super(customerProperties, notificationDataService, extensionConfigDataService, emailMessagingService,
				dataServicesFactory);

		final String quartzTriggerPropValue = getCustomerProperties().getNotifierVariableProperties()
				.get(getNotifierPropertyKey() + ".cron.expression");
		cronExpression = StringUtils.trimToNull(quartzTriggerPropValue);
	}

	public abstract DateRange createDateRange();

	public abstract String getCategory();

	@Override
	public void run() {
		try {
			final ExtensionProperties globalConfig = createPropertiesFromGlobalConfig();
			final List<UserConfigItem> userConfigList = getExtensionConfigDataService()
					.getUserConfigList(getHubExtensionUri());
			final List<UserConfigItem> usersInCategory = createUserListInCategory(userConfigList);

			if (!usersInCategory.isEmpty()) {
				final DateRange dateRange = createDateRange();
				final Date startDate = dateRange.getStart();
				final Date endDate = dateRange.getEnd();
				final SortedSet<NotificationContentItem> notifications = getNotificationDataService()
						.getAllNotifications(startDate, endDate);
				final NotificationProcessor processor = new NotificationProcessor(getDataServicesFactory());
				final Collection<ProjectData> projectList = processor.process(notifications);
				if (!projectList.isEmpty()) {
					for (final UserConfigItem userConfig : usersInCategory) {
						try {
							// TODO need to simplify this more. too
							// expensive
							final Collection<ProjectData> projectsDigest = filterCategories(projectList, userConfig);
							final Map<String, Object> model = new HashMap<>();
							model.put(KEY_TOPICS_LIST, projectsDigest);
							model.put(KEY_START_DATE, String.valueOf(startDate));
							model.put(KEY_END_DATE, String.valueOf(endDate));
							model.put(KEY_USER_FIRST_NAME, userConfig.getUser().getFirstName());
							model.put(KEY_USER_LAST_NAME, userConfig.getUser().getLastName());
							model.put(KEY_NOTIFIER_CATEGORY, getCategory().toUpperCase());
							final String emailAddress = userConfig.getUser().getEmail();
							final String templateName = getTemplateName(userConfig);
							final EmailTarget emailTarget = new EmailTarget(emailAddress, templateName, model);
							getEmailMessagingService().sendEmailMessage(emailTarget, globalConfig);
						} catch (final Exception e) {
							logger.error("Error sending email to user", e);
						}
					}
				}
			}
		} catch (final Exception e) {
			logger.error("Error sending the email", e);
		}
	}

	private Collection<ProjectData> filterCategories(final Collection<ProjectData> projectList,
			final UserConfigItem userConfig) {
		// final List<ProjectData> filteredList = new ArrayList<>();
		// for (int index = 0; index < 5; index++) {
		// final List<ItemData> itemList = new ArrayList<>(15);
		// for (int itemIndex = 0; itemIndex < 15; itemIndex++) {
		// final Map<String, String> dataMap = new HashMap<>();
		// dataMap.put("KEY_" + itemIndex, "VALUE_" + itemIndex);
		// itemList.add(new ItemData(dataMap));
		// }
		// final List<CategoryData> categoryMap = new ArrayList<>();
		// for (int catIndex = 0; catIndex < 5; catIndex++) {
		// final String category = "CATEGORY_" + catIndex;
		// categoryMap.add(new CategoryData(category, itemList));
		// }
		// filteredList.add(new ProjectData("PROJECT_NAME>PROJECT_VERSION",
		// categoryMap));
		// }

		return projectList;
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

	private Set<String> getTriggerSet(final UserConfigItem userConfig) {
		final List<String> triggerList = getConfigValueList(userConfig, EmailExtensionConstants.CONFIG_KEY_TRIGGERS);
		final Set<String> triggerSet = new HashSet<>();
		triggerSet.addAll(triggerList);
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
		final Map<String, ConfigurationItem> globalMap = getExtensionConfigDataService()
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
