package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.AbstractTransform;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.api.notification.PolicyOverrideNotificationItem;
import com.blackducksoftware.integration.hub.api.notification.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilityNotificationItem;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class DailyDigestRouter extends AbstractEmailRouter<NotificationItem> {

	private final static String LIST_POLICY_VIOLATIONS = "policyViolations";
	private final static String LIST_POLICY_OVERRIDES = "policyViolationOverrides";
	private final static String LIST_POLICY_OVERRIDE_CANCEL = "policyViolationOverrides";
	private final static String LIST_VULNERABILITIES = "securityVulnerabilities";

	public DailyDigestRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractTransform> transformMap, final EmailTaskData taskData) {
		super(emailMessagingService, customerProperties, notificationService, transformMap, taskData);
	}

	@Override
	public EmailData transform(final List<NotificationItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> templateMap = initTempateMap();
		final Map<String, AbstractTransform> transformMap = getTransformMap();
		for (final NotificationItem notification : data) {
			final Class<?> key = notification.getClass();
			if (key.equals(RuleViolationNotificationItem.class)) {
				if (transformMap.containsKey(key.getName())) {
					final AbstractTransform converter = transformMap.get(key.getName());
					processRuleViolation(converter, templateMap, notification);
				}
			} else if (key.equals(PolicyOverrideNotificationItem.class)) {
				if (transformMap.containsKey(key.getName())) {
					final AbstractTransform converter = transformMap.get(key.getName());
					processPolicyOverride(converter, templateMap, notification);
				}
			} else if (key.equals(VulnerabilityNotificationItem.class)) {
				if (transformMap.containsKey(key.getName())) {
					final AbstractTransform converter = transformMap.get(key.getName());
					processVulnerabilities(converter, templateMap, notification);
				}
			}
		}
		return new EmailData(addresses, templateMap);
	}

	public Map<String, Object> initTempateMap() {
		final Map<String, Object> templateMap = new HashMap<>();
		templateMap.put(KEY_USER, "Hub user");
		templateMap.put(KEY_HUB_URL, "_blank");
		templateMap.put(LIST_POLICY_VIOLATIONS, new ArrayList<Map<String, Object>>());
		templateMap.put(LIST_POLICY_OVERRIDES, new ArrayList<Map<String, Object>>());
		templateMap.put(LIST_POLICY_OVERRIDE_CANCEL, new ArrayList<Map<String, Object>>());
		templateMap.put(LIST_VULNERABILITIES, new ArrayList<Map<String, Object>>());
		return templateMap;
	}

	@SuppressWarnings("unchecked")
	private void processRuleViolation(final AbstractTransform converter, final Map<String, Object> templateMap,
			final NotificationItem item) {
		final List<Map<String, Object>> violationList = (List<Map<String, Object>>) templateMap
				.get(LIST_POLICY_VIOLATIONS);
		violationList.addAll(converter.transform(item));
	}

	@SuppressWarnings("unchecked")
	private void processPolicyOverride(final AbstractTransform converter, final Map<String, Object> templateMap,
			final NotificationItem item) {
		final List<Map<String, Object>> policyOverrides = (List<Map<String, Object>>) templateMap
				.get(LIST_POLICY_OVERRIDES);

		policyOverrides.addAll(converter.transform(item));
		// final List<Map<String, Object>> policyOverrideCancels =
		// (List<Map<String, Object>>) templateMap
		// .get(LIST_POLICY_OVERRIDE_CANCEL);
	}

	@SuppressWarnings("unchecked")
	private void processVulnerabilities(final AbstractTransform converter, final Map<String, Object> templateMap,
			final NotificationItem item) {
		final List<Map<String, Object>> vulnerabilityList = (List<Map<String, Object>>) templateMap
				.get(LIST_VULNERABILITIES);
		vulnerabilityList.addAll(converter.transform(item));
	}

	@Override
	public String getTemplateName() {
		return "dailyDigest.ftl";
	}
}
