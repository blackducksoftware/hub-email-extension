package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyViolationContentItem;

public class PolicyViolationRouter extends AbstractEmailRouter<PolicyViolationContentItem> {
	private final static String LIST_POLICY_VIOLATIONS = "policyViolations";

	public PolicyViolationRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final Map<String, AbstractContentTransform> transformMap,
			final String templateName, final EmailTaskData taskData) {
		super(emailMessagingService, customerProperties, transformMap, templateName, taskData);
	}

	@Override
	public EmailData transform(final List<PolicyViolationContentItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> templateMap = initTempateMap();
		final Map<String, AbstractContentTransform> transformMap = getTransformMap();
		for (final PolicyViolationContentItem item : data) {
			final Class<?> key = item.getClass();
			if (transformMap.containsKey(key.getName())) {
				final AbstractContentTransform converter = transformMap.get(key.getName());
				processRuleViolation(converter, templateMap, item);
			}
		}
		return new EmailData(addresses, templateMap);
	}

	public Map<String, Object> initTempateMap() {
		final Map<String, Object> templateMap = new HashMap<>();
		templateMap.put(KEY_USER, "Hub user");
		templateMap.put(LIST_POLICY_VIOLATIONS, new ArrayList<Map<String, Object>>());
		return templateMap;
	}

	@SuppressWarnings("unchecked")
	private void processRuleViolation(final AbstractContentTransform converter, final Map<String, Object> templateMap,
			final PolicyViolationContentItem item) {
		final List<Map<String, Object>> violationList = (List<Map<String, Object>>) templateMap
				.get(LIST_POLICY_VIOLATIONS);
		violationList.addAll(converter.transform(item));
	}
}
