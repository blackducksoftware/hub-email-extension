package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailExtensionConstants;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.ProjectDigest;
import com.blackducksoftware.integration.email.model.ProjectsDigest;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transformer.NotificationCountTransformer;
import com.blackducksoftware.integration.hub.api.extension.ConfigurationItem;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.extension.item.UserConfigItem;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ProjectAggregateData;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;

public abstract class AbstractDigestNotifier extends AbstractNotifier {
	private static final String KEY_PROJECT_DIGEST = "projectsDigest";
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";
	public static final String KEY_TOTAL_NOTIFICATIONS = "totalNotifications";
	public static final String KEY_TOTAL_POLICY_VIOLATIONS = "totalPolicyViolations";
	public static final String KEY_TOTAL_POLICY_OVERRIDES = "totalPolicyOverrides";
	public static final String KEY_TOTAL_VULNERABILITIES = "totalVulnerabilities";
	public static final String KEY_CATEGORY = "emailCategory";

	private final Logger logger = LoggerFactory.getLogger(AbstractDigestNotifier.class);
	private final String cronExpression;

	public AbstractDigestNotifier(final ExtensionProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, extensionConfigDataService, emailMessagingService);

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
				final List<ProjectAggregateData> notifications = getNotificationDataService()
						.getNotificationCounts(startDate, endDate);
				if (!notifications.isEmpty()) {
					for (final UserConfigItem userConfig : usersInCategory) {
						try {
							// TODO need to simplify this more. too
							// expensive
							final ProjectsDigest projectsDigest = createMap(notifications, userConfig);
							final Map<String, Object> model = new HashMap<>();
							model.put(KEY_PROJECT_DIGEST, projectsDigest);
							model.put(KEY_START_DATE, String.valueOf(startDate));
							model.put(KEY_END_DATE, String.valueOf(endDate));
							model.put("hubUserName", userConfig.getUser().getUserName());
							model.put(KEY_CATEGORY, getCategory());
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

	private ProjectsDigest createMap(final List<ProjectAggregateData> notifications, final UserConfigItem userConfig) {
		final Set<String> triggerSet = getTriggerSet(userConfig);
		final boolean includePolicyViolations = triggerSet.contains("policyViolation");
		final boolean includePolicyOverrides = triggerSet.contains("overridePolicyViolation");
		final boolean includePolicyCleared = false; // not supported yet
		final boolean includeVulnerabilities = triggerSet.contains("securityVulnerabilities");
		final NotificationCountTransformer transformer = new NotificationCountTransformer(includePolicyViolations,
				includePolicyOverrides, includePolicyCleared, includeVulnerabilities);
		final List<ProjectDigest> projectData = new ArrayList<>();
		int totalNotifications = 0;
		int totalPolicyViolations = 0;
		int totalPolicyOverrides = 0;
		int totalVulnerabilities = 0;
		for (final ProjectAggregateData notification : notifications) {
			totalNotifications += notification.getTotal();
			totalPolicyViolations += notification.getPolicyViolationCount();
			totalPolicyOverrides += notification.getPolicyOverrideCount();
			totalVulnerabilities += notification.getVulnerabilityCount();
			projectData.add(transformer.transform(notification));
		}

		final Map<String, String> totalsMap = new HashMap<>();
		totalsMap.put(KEY_TOTAL_NOTIFICATIONS, String.valueOf(totalNotifications));
		totalsMap.put(KEY_TOTAL_POLICY_VIOLATIONS, String.valueOf(totalPolicyViolations));
		totalsMap.put(KEY_TOTAL_POLICY_OVERRIDES, String.valueOf(totalPolicyOverrides));
		totalsMap.put(KEY_TOTAL_VULNERABILITIES, String.valueOf(totalVulnerabilities));
		final ProjectsDigest digest = new ProjectsDigest(totalsMap, projectData);
		return digest;
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
