package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailExtensionConstants;
import com.blackducksoftware.integration.email.EmailFrequencyCategory;
import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ProjectDigest;
import com.blackducksoftware.integration.email.model.ProjectsDigest;
import com.blackducksoftware.integration.email.model.UserPreferences;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transformer.NotificationCountTransformer;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.extension.items.UserConfigItem;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ProjectAggregateData;

public abstract class AbstractDigestRouter extends AbstractRouter {
	private static final String KEY_PROJECT_DIGEST = "projectsDigest";
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";
	public static final String KEY_TOTAL_NOTIFICATIONS = "totalNotifications";
	public static final String KEY_TOTAL_POLICY_VIOLATIONS = "totalPolicyViolations";
	public static final String KEY_TOTAL_POLICY_OVERRIDES = "totalPolicyOverrides";
	public static final String KEY_TOTAL_VULNERABILITIES = "totalVulnerabilities";

	private final Logger logger = LoggerFactory.getLogger(AbstractDigestRouter.class);
	private final String cronExpression;

	public AbstractDigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, extensionConfigDataService, emailMessagingService);

		final String quartzTriggerPropValue = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".cron.expression");
		cronExpression = StringUtils.trimToNull(quartzTriggerPropValue);
	}

	public abstract DateRange createDateRange();

	public abstract EmailFrequencyCategory getCategory();

	@Override
	public void run() {
		try {
			final DateRange dateRange = createDateRange();
			final Date startDate = dateRange.getStart();
			final Date endDate = dateRange.getEnd();
			final List<ProjectAggregateData> notifications = getNotificationDataService()
					.getNotificationCounts(startDate, endDate);
			if (!notifications.isEmpty()) {
				final List<UserConfigItem> userConfigList = getExtensionConfigDataService()
						.getUserOverrideConfigList(getHubExtensionId());
				final UserPreferences userPreferences = new UserPreferences(getCustomerProperties());
				for (final UserConfigItem userConfig : userConfigList) {
					final boolean optedIn = isOptedIn(userConfig);
					final boolean frequencyMatch = doesCategoryMatch(userConfig);
					if (optedIn && frequencyMatch) {
						try {
							// TODO need to simplify this more. too expensive
							final ProjectsDigest projectsDigest = createMap(notifications, userConfig);
							final Map<String, Object> model = new HashMap<>();
							model.put(KEY_PROJECT_DIGEST, projectsDigest);
							model.put(KEY_START_DATE, String.valueOf(startDate));
							model.put(KEY_END_DATE, String.valueOf(endDate));
							model.put("hubUserName", userConfig.getUser().getUserName());
							final String emailAddress = userConfig.getUser().getEmail();
							final String templateName = getTemplateName(userConfig);
							final EmailTarget emailTarget = new EmailTarget(emailAddress, templateName, model);
							if (!userPreferences.isOptedOut(emailAddress, templateName)) {
								getEmailMessagingService().sendEmailMessage(emailTarget);
							}
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
		final EmailFrequencyCategory configFrequency = EmailFrequencyCategory.getEmailFrequency(emailFrequency);

		return getCategory() == configFrequency;
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

	@Override
	public String getTemplateName() {
		return "digest.ftl";
	}

	@Override
	public String getCronExpression() {
		return cronExpression;
	}
}
