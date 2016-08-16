package com.blackducksoftware.integration.email.transformer;

import java.util.Map;

import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyViolationContentItem;

public class PolicyViolationTransformer extends NotificationTransformer {
	@Override
	public FreemarkerTarget transform(final NotificationContentItem notification) {
		final FreemarkerTarget templateData = new FreemarkerTarget();

		if (notification instanceof PolicyViolationContentItem) {
			final PolicyViolationContentItem policyViolationContentItem = (PolicyViolationContentItem) notification;
			for (final String policyName : policyViolationContentItem.getPolicyNameList()) {
				final Map<String, String> map = transformNotificationContentItem(policyViolationContentItem);
				map.put(KEY_POLICY_NAME, policyName);
				templateData.add(map);
			}
		}

		return templateData;
	}

}
