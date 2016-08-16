package com.blackducksoftware.integration.email.notifier.routers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.FreemarkerTarget;
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

public class DigestRouter extends AbstractRouter {
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

	private static final String LIST_POLICY_VIOLATIONS = "policyViolations";
	private static final String LIST_POLICY_OVERRIDES = "policyViolationOverrides";
	private static final String LIST_VULNERABILITIES = "securityVulnerabilities";

	public DigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, userRestService, emailMessagingService);
	}

	@Override
	public void run() {
		try {
			final DateFormat df = getDateFormat();
			Date startDate = null;
			final File lastRunFile = new File("/Users/ekerwin/Documents/email_ext/digestLastRun.txt");
			if (lastRunFile.exists()) {
				final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
				startDate = df.parse(lastRunValue);
				startDate = new Date(startDate.getTime() + 1);
			} else {
				final String lastRunValue = getCustomerProperties().getProperty("hub.email.digest.start.date");
				startDate = df.parse(lastRunValue);
			}

			final Date endDate = new Date();
			FileUtils.write(lastRunFile, df.format(endDate), "UTF-8");

			final List<NotificationContentItem> notifications = getNotificationDataService().getNotifications(startDate,
					endDate, -1);
			final List<UserItem> users = getUserRestService().getAllUsers();
		} catch (final Exception e) {
			// do something intelligent
		}
	}

	private DateFormat getDateFormat() {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf;
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

		}
		final Map<String, FreemarkerTarget> freemarkerMap = new HashMap<>();
		freemarkerMap.put(LIST_POLICY_VIOLATIONS, policyViolations);
		freemarkerMap.put(LIST_POLICY_OVERRIDES, policyOverrides);
		freemarkerMap.put(LIST_VULNERABILITIES, vulnerabilities);
		return freemarkerMap;
	}

	@Override
	public String getRouterKey() {
		return "dailyDigest.ftl";
	}

	@Override
	public long getIntervalMilliseconds() {
		// TODO fix the interval
		return 5000;
	}

}
