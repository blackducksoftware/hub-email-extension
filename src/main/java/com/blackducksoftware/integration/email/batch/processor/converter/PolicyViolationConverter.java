package com.blackducksoftware.integration.email.batch.processor.converter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.email.batch.processor.NotificationCategory;
import com.blackducksoftware.integration.email.batch.processor.NotificationEvent;
import com.blackducksoftware.integration.email.batch.processor.NotificationItemType;
import com.blackducksoftware.integration.email.batch.processor.ProcessingAction;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;

public class PolicyViolationConverter implements IItemConverter {

	@Override
	public List<NotificationEvent> convert(final NotificationContentItem notification) {
		final List<NotificationEvent> itemList = new LinkedList<>();
		if (notification instanceof PolicyViolationContentItem) {
			final PolicyViolationContentItem policyViolationContentItem = (PolicyViolationContentItem) notification;
			for (final PolicyRule rule : policyViolationContentItem.getPolicyRuleList()) {
				final ProjectVersion projectVersion = policyViolationContentItem.getProjectVersion();
				final String componentName = policyViolationContentItem.getComponentName();
				final String componentVersion = policyViolationContentItem.getComponentVersion();
				final Set<ItemEntry> dataMap = new LinkedHashSet<>(4);
				dataMap.add(new ItemEntry(NotificationItemType.ITEM_TYPE_RULE.name(), rule.getName()));
				dataMap.add(new ItemEntry(NotificationItemType.ITEM_TYPE_COMPONENT.name(), componentName));
				dataMap.add(new ItemEntry("", componentVersion));
				itemList.add(new NotificationEvent(ProcessingAction.ADD, projectVersion.getProjectName(),
						projectVersion.getProjectVersionName(), componentName, componentVersion,
						NotificationCategory.CATEGORY_POLICY_VIOLATION, dataMap, Collections.emptySet()));
			}
		}
		return itemList;
	}
}
