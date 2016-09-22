package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ProjectDigest;
import com.blackducksoftware.integration.email.model.ProjectsDigest;
import com.blackducksoftware.integration.email.model.UserPreferences;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transformer.NotificationCountTransformer;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.api.user.UserItem;
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
	private long interval;

	public AbstractDigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, userRestService, emailMessagingService);
		final String intervalPropValue = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".interval.in.milliseconds");
		final String intervalString = StringUtils.trimToNull(intervalPropValue);
		if (intervalString != null) {
			try {
				interval = Long.valueOf(intervalString);
			} catch (final NumberFormatException e) {
				interval = 0;
			}
		} else {
			interval = 0;
		}
	}

	public abstract DateRange createDateRange();

	@Override
	public void run() {
		try {
			final DateRange dateRange = createDateRange();
			final Date startDate = dateRange.getStart();
			final Date endDate = dateRange.getEnd();
			final List<ProjectAggregateData> notifications = getNotificationDataService()
					.getNotificationCounts(startDate, endDate);
			if (!notifications.isEmpty()) {
				final ProjectsDigest projectsDigest = createMap(notifications);
				final List<UserItem> users = getUserRestService().getAllUsers();
				final UserPreferences userPreferences = new UserPreferences(getCustomerProperties());
				for (final UserItem user : users) {
					try {
						final Map<String, Object> model = new HashMap<>();
						model.put(KEY_PROJECT_DIGEST, projectsDigest);
						model.put(KEY_START_DATE, String.valueOf(startDate));
						model.put(KEY_END_DATE, String.valueOf(endDate));
						model.put("hubUserName", user.getUserName());
						final String emailAddress = user.getEmail();
						final String templateName = getTemplateName();
						final EmailTarget emailTarget = new EmailTarget(emailAddress, templateName, model);
						if (!userPreferences.isOptedOut(emailAddress, templateName)) {
							getEmailMessagingService().sendEmailMessage(emailTarget);
						}
					} catch (final Exception e) {
						logger.error("Error sending email to user", e);
					}
				}
			}
		} catch (final Exception e) {
			logger.error("Error sending the email", e);
		}
	}

	private ProjectsDigest createMap(final List<ProjectAggregateData> notifications) {
		final NotificationCountTransformer transformer = new NotificationCountTransformer();
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

	@Override
	public String getTemplateName() {
		return "digest.ftl";
	}

	@Override
	public long getIntervalMilliseconds() {
		return interval;
	}
}
