package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.VulnerabilityContentItem;

public class DailyDigestRouter extends AbstractEmailRouter<NotificationContentItem> {
	private final static String LIST_POLICY_VIOLATIONS = "policyViolations";
	private final static String LIST_POLICY_OVERRIDES = "policyViolationOverrides";
	private final static String LIST_POLICY_OVERRIDE_CANCEL = "policyViolationOverrides";
	private final static String LIST_VULNERABILITIES = "securityVulnerabilities";

	public DailyDigestRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final Map<String, AbstractContentTransform> transformMap,
			final String templateName, final EmailTaskData taskData) {
		super(emailMessagingService, customerProperties, transformMap, templateName, taskData);
	}

	@Override
	public EmailData transform(final List<NotificationContentItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> templateMap = initTempateMap();
		final Map<String, AbstractContentTransform> transformMap = getTransformMap();
		for (final NotificationContentItem item : data) {
			final Class<?> key = item.getClass();
			if (key.equals(PolicyViolationContentItem.class)) {
				if (transformMap.containsKey(key.getName())) {
					final AbstractContentTransform converter = transformMap.get(key.getName());
					processRuleViolation(converter, templateMap, item);
				}
			} else if (key.equals(PolicyOverrideContentItem.class)) {
				if (transformMap.containsKey(key.getName())) {
					final AbstractContentTransform converter = transformMap.get(key.getName());
					processPolicyOverride(converter, templateMap, item);
				}
			} else if (key.equals(VulnerabilityContentItem.class)) {
				if (transformMap.containsKey(key.getName())) {
					final AbstractContentTransform converter = transformMap.get(key.getName());
					processVulnerabilities(converter, templateMap, item);
				}
			}
		}
		return new EmailData(addresses, templateMap);
	}

	public Map<String, Object> initTempateMap() {
		final Map<String, Object> templateMap = new HashMap<>();
		templateMap.put(KEY_USER, "Hub user");
		templateMap.put(LIST_POLICY_VIOLATIONS, new ArrayList<Map<String, Object>>());
		templateMap.put(LIST_POLICY_OVERRIDES, new ArrayList<Map<String, Object>>());
		templateMap.put(LIST_POLICY_OVERRIDE_CANCEL, new ArrayList<Map<String, Object>>());
		templateMap.put(LIST_VULNERABILITIES, new ArrayList<Map<String, Object>>());
		return templateMap;
	}

	@SuppressWarnings("unchecked")
	private void processRuleViolation(final AbstractContentTransform converter, final Map<String, Object> templateMap,
			final NotificationContentItem item) {
		final List<Map<String, Object>> violationList = (List<Map<String, Object>>) templateMap
				.get(LIST_POLICY_VIOLATIONS);
		violationList.addAll(converter.transform(item));
	}

	@SuppressWarnings("unchecked")
	private void processPolicyOverride(final AbstractContentTransform converter, final Map<String, Object> templateMap,
			final NotificationContentItem item) {
		final List<Map<String, Object>> policyOverrides = (List<Map<String, Object>>) templateMap
				.get(LIST_POLICY_OVERRIDES);

		policyOverrides.addAll(converter.transform(item));
	}

	@SuppressWarnings("unchecked")
	private void processVulnerabilities(final AbstractContentTransform converter, final Map<String, Object> templateMap,
			final NotificationContentItem item) {
		final List<Map<String, Object>> vulnerabilityList = (List<Map<String, Object>>) templateMap
				.get(LIST_VULNERABILITIES);
		vulnerabilityList.addAll(converter.transform(item));
	}

}
