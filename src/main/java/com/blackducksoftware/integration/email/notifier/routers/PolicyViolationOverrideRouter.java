package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailContentItem;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.model.PolicyOverrideContentItem;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class PolicyViolationOverrideRouter extends AbstractEmailRouter<PolicyOverrideContentItem> {
	private final static String LIST_POLICY_OVERRIDES = "policyViolationOverrides";

	public PolicyViolationOverrideRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractContentTransform> transformMap, final String templateName,
			final EmailTaskData taskData) {
		super(emailMessagingService, customerProperties, notificationService, transformMap, templateName, taskData);
	}

	@Override
	public EmailData transform(final List<PolicyOverrideContentItem> data) {
		final List<String> addresses = new ArrayList<>();
		addresses.add("psantos@blackducksoftware.com");
		final Map<String, Object> templateMap = initTempateMap();
		final Map<String, AbstractContentTransform> transformMap = getTransformMap();
		for (final PolicyOverrideContentItem item : data) {
			final Class<?> key = item.getClass();
			if (transformMap.containsKey(key.getName())) {
				final AbstractContentTransform converter = transformMap.get(key.getName());
				processPolicyOverride(converter, templateMap, item);
			}
		}
		return new EmailData(addresses, templateMap);
	}

	public Map<String, Object> initTempateMap() {
		final Map<String, Object> templateMap = new HashMap<>();
		templateMap.put(KEY_USER, "Hub user");
		templateMap.put(LIST_POLICY_OVERRIDES, new ArrayList<Map<String, Object>>());
		return templateMap;
	}

	@SuppressWarnings("unchecked")
	private void processPolicyOverride(final AbstractContentTransform converter, final Map<String, Object> templateMap,
			final EmailContentItem item) {
		final List<Map<String, Object>> policyOverrides = (List<Map<String, Object>>) templateMap
				.get(LIST_POLICY_OVERRIDES);

		policyOverrides.addAll(converter.transform(item));
	}
}
