package com.blackducksoftware.integration.email.notifier.routers;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.email.model.UserPreferences;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transformer.NotificationTransformer;
import com.blackducksoftware.integration.email.transformer.PolicyOverrideTransformer;
import com.blackducksoftware.integration.email.transformer.PolicyViolationTransformer;
import com.blackducksoftware.integration.email.transformer.VulnerabilityTransformer;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.api.user.UserItem;
import com.blackducksoftware.integration.hub.dataservices.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class DigestRouter extends AbstractRouter {
	private static final String LIST_POLICY_VIOLATIONS = "policyViolations";
	private static final String LIST_POLICY_OVERRIDES = "policyViolationOverrides";
	private static final String LIST_VULNERABILITIES = "securityVulnerabilities";

	private final Logger logger = LoggerFactory.getLogger(DigestRouter.class);
	private final long interval;
	private final String lastRunPath;
	private final String initialStartDate;

	public DigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, userRestService, emailMessagingService);
		interval = Long.valueOf(
				getCustomerProperties().getRouterVariableProperties().get(getRouterPropertyKey() + ".lastrun.file"));
		lastRunPath = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".interval.in.milliseconds");
		initialStartDate = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".start.date");
	}

	@Override
	public void run() {
		try {
			Date startDate = null;
			final File lastRunFile = new File(getCustomerProperties().getProperty(lastRunPath));
			if (lastRunFile.exists()) {
				final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
				startDate = RestConnection.parseDateString(lastRunValue);
				startDate = new Date(startDate.getTime() + 1);
			} else {
				final String lastRunValue = getCustomerProperties().getProperty(initialStartDate);
				startDate = RestConnection.parseDateString(lastRunValue);
			}

			Date endDate = new Date();
			FileUtils.write(lastRunFile, RestConnection.formatDate(endDate), "UTF-8");
			startDate = RestConnection.parseDateString("2016-08-16T00:00:00.000Z");
			endDate = RestConnection.parseDateString("2016-08-17T00:14:10.859Z");
			final List<NotificationContentItem> notifications = getNotificationDataService()
					.getAllNotifications(startDate, endDate);
			final Map<String, FreemarkerTarget> notificationsMap = createMap(notifications);
			final List<UserItem> users = getUserRestService().getAllUsers();
			final UserPreferences userPreferences = new UserPreferences(getCustomerProperties());
			for (final UserItem user : users) {
				try {
					final Map<String, Object> model = new HashMap<>();
					model.putAll(notificationsMap);
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
		} catch (final Exception e) {
			logger.error("Error sending the email", e);
		}
	}

	private Map<String, FreemarkerTarget> createMap(final List<NotificationContentItem> notifications) {
		final FreemarkerTarget policyViolations = new FreemarkerTarget();
		final FreemarkerTarget policyOverrides = new FreemarkerTarget();
		final FreemarkerTarget vulnerabilities = new FreemarkerTarget();

		final Map<Class<? extends NotificationContentItem>, FreemarkerTarget> freemarkerTargets = new HashMap<>();
		freemarkerTargets.put(PolicyViolationContentItem.class, policyViolations);
		freemarkerTargets.put(PolicyOverrideContentItem.class, policyOverrides);
		freemarkerTargets.put(VulnerabilityContentItem.class, vulnerabilities);

		final Map<Class<? extends NotificationContentItem>, NotificationTransformer> transformers = new HashMap<>();
		transformers.put(PolicyViolationContentItem.class, new PolicyViolationTransformer());
		transformers.put(PolicyOverrideContentItem.class, new PolicyOverrideTransformer());
		transformers.put(VulnerabilityContentItem.class, new VulnerabilityTransformer());

		for (final NotificationContentItem notification : notifications) {
			final FreemarkerTarget freemarkerTarget = freemarkerTargets.get(notification.getClass());
			final NotificationTransformer notificationTransformer = transformers.get(notification.getClass());
			freemarkerTarget.addAll(notificationTransformer.transform(notification));
		}

		final Map<String, FreemarkerTarget> freemarkerMap = new HashMap<>();
		freemarkerMap.put(LIST_POLICY_VIOLATIONS, policyViolations);
		freemarkerMap.put(LIST_POLICY_OVERRIDES, policyOverrides);
		freemarkerMap.put(LIST_VULNERABILITIES, vulnerabilities);
		return freemarkerMap;
	}

	@Override
	public String getTemplateName() {
		return "digest.ftl";
	}

	@Override
	public String getRouterPropertyKey() {
		return "digest";
	}

	@Override
	public long getIntervalMilliseconds() {
		return interval;
	}
}
