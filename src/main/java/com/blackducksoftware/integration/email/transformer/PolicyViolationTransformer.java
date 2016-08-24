package com.blackducksoftware.integration.email.transformer;

import java.util.Map;

import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;

public class PolicyViolationTransformer extends NotificationTransformer {
	@Override
	public FreemarkerTarget transform(final NotificationContentItem notification) {
		final FreemarkerTarget templateData = new FreemarkerTarget();

		if (notification instanceof PolicyViolationContentItem) {
			final PolicyViolationContentItem policyViolationContentItem = (PolicyViolationContentItem) notification;
			for (final PolicyRule policyRule : policyViolationContentItem.getPolicyRuleList()) {
				final Map<String, String> map = transformNotificationContentItem(policyViolationContentItem);
				map.put(KEY_POLICY_NAME, policyRule.getName());
				templateData.add(map);
			}
		}

		return templateData;
	}

}
