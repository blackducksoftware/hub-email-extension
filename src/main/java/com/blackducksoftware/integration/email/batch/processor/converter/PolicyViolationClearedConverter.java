package com.blackducksoftware.integration.email.batch.processor.converter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;
import com.blackducksoftware.integration.email.batch.processor.NotificationEvent;
import com.blackducksoftware.integration.email.batch.processor.ItemTypeEnum;
import com.blackducksoftware.integration.email.batch.processor.ProcessingAction;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
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
				final Set<ItemEntry> dataMap = new LinkedHashSet<>(4);
				dataMap.add(new ItemEntry(ItemTypeEnum.RULE.name(), rule.getName()));
				dataMap.add(new ItemEntry(ItemTypeEnum.COMPONENT.name(), componentName));
				dataMap.add(new ItemEntry("", componentVersion));
				itemList.add(new NotificationEvent(ProcessingAction.REMOVE, projectVersion.getProjectName(),
						projectVersion.getProjectVersionName(), componentName, componentVersion,
						NotificationCategoryEnum.POLICY_VIOLATION, dataMap, Collections.emptySet()));
			}
		}
		return itemList;
	}

}
