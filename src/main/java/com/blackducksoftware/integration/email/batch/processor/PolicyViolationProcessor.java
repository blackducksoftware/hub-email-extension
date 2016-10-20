package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;

public class PolicyViolationProcessor extends NotificationSubProcessor {

	public PolicyViolationProcessor(final SubProcessorCache cache) {
		super(cache);
	}

	@Override
	public void process(final NotificationContentItem notification) {
		if (notification instanceof PolicyViolationContentItem) {
			final PolicyViolationContentItem policyViolationContentItem = (PolicyViolationContentItem) notification;
			for (final PolicyRule rule : policyViolationContentItem.getPolicyRuleList()) {
				final ProjectVersion projectVersion = policyViolationContentItem.getProjectVersion();
				final String projectName = projectVersion.getProjectName();
				final String projectVersionName = projectVersion.getProjectVersionName();
				final String componentName = policyViolationContentItem.getComponentName();
				final String componentVersion = policyViolationContentItem.getComponentVersion();
				final String eventKey = generateEventKey(projectName, projectVersionName, componentName,
						componentVersion, NotificationCategoryEnum.POLICY_VIOLATION.name());
				final Set<ItemEntry> dataMap = new LinkedHashSet<>(4);
				dataMap.add(new ItemEntry(ItemTypeEnum.RULE.name(), rule.getName()));
				dataMap.add(new ItemEntry(ItemTypeEnum.COMPONENT.name(), componentName));
				dataMap.add(new ItemEntry("", componentVersion));
				final NotificationEvent event = new NotificationEvent(ProcessingAction.ADD,
						projectVersion.getProjectName(), projectVersion.getProjectVersionName(), componentName,
						componentVersion, eventKey, NotificationCategoryEnum.POLICY_VIOLATION, dataMap,
						Collections.emptySet());
				getCache().addEvent(event);
			}
		}
	}
}
