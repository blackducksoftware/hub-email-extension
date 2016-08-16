package com.blackducksoftware.integration.email.transformer;

import java.util.Map;

import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyOverrideContentItem;

public class PolicyOverrideTransformer extends NotificationTransformer {
	@Override
	public FreemarkerTarget transform(final NotificationContentItem notification) {
		final FreemarkerTarget templateData = new FreemarkerTarget();

		if (notification instanceof PolicyOverrideContentItem) {
			final PolicyOverrideContentItem policyOverrideContentItem = (PolicyOverrideContentItem) notification;
			for (final String policyName : policyOverrideContentItem.getPolicyNameList()) {
				final Map<String, String> map = transformNotificationContentItem(policyOverrideContentItem);
				map.put(KEY_POLICY_NAME, policyName);
				map.put(KEY_FIRST_NAME, policyOverrideContentItem.getFirstName());
				map.put(KEY_LAST_NAME, policyOverrideContentItem.getLastName());
				templateData.add(map);
			}
		}

		return templateData;
	}

}
