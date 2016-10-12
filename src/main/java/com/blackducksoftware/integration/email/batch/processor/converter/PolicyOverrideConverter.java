package com.blackducksoftware.integration.email.batch.processor.converter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.batch.processor.NotificationCategory;
import com.blackducksoftware.integration.email.batch.processor.NotificationEvent;
import com.blackducksoftware.integration.email.batch.processor.NotificationItemType;
import com.blackducksoftware.integration.email.batch.processor.ProcessingAction;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;

public class PolicyOverrideConverter implements IItemConverter {

	@Override
	public List<NotificationEvent> convert(final NotificationContentItem notification) {
		final List<NotificationEvent> itemList = new LinkedList<>();
		if (notification instanceof PolicyOverrideContentItem) {
			final PolicyOverrideContentItem policyOverrideContentItem = (PolicyOverrideContentItem) notification;
			for (final PolicyRule rule : policyOverrideContentItem.getPolicyRuleList()) {
				final ProjectVersion projectVersion = policyOverrideContentItem.getProjectVersion();
				final String componentName = policyOverrideContentItem.getComponentName();
				final String componentVersion = policyOverrideContentItem.getComponentVersion();
				final Map<String, String> dataMap = new LinkedHashMap<>(4);
				dataMap.put(rule.getName(), NotificationItemType.ITEM_TYPE_RULE.name());
				dataMap.put(componentName, NotificationItemType.ITEM_TYPE_COMPONENT.name());
				dataMap.put(componentVersion, "");
				itemList.add(new NotificationEvent(ProcessingAction.REMOVE, projectVersion.getProjectName(),
						projectVersion.getProjectVersionName(), componentName, componentVersion,
						NotificationCategory.CATEGORY_POLICY_VIOLATON, dataMap, Collections.emptySet()));
			}
		}
		return itemList;
	}

}
