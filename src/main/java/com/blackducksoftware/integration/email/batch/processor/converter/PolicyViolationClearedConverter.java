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
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;

public class PolicyViolationClearedConverter implements IItemConverter {

	@Override
	public List<NotificationEvent> convert(final NotificationContentItem notification) {
		final List<NotificationEvent> itemList = new LinkedList<>();
		if (notification instanceof PolicyViolationClearedContentItem) {
			final PolicyViolationClearedContentItem policyViolationCleared = (PolicyViolationClearedContentItem) notification;
			for (final PolicyRule rule : policyViolationCleared.getPolicyRuleList()) {
				final ProjectVersion projectVersion = policyViolationCleared.getProjectVersion();
				final String componentName = policyViolationCleared.getComponentName();
				final String componentVersion = policyViolationCleared.getComponentVersion();
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
