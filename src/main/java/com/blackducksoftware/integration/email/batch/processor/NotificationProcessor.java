package com.blackducksoftware.integration.email.batch.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

public class NotificationProcessor {
	private final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);
	private final Set<Class<?>> policyViolationTypeSet = new HashSet<>();
	private final Set<Class<?>> vulnerabilityTypeSet = new HashSet<>();
	private final Map<Class<?>, ProcessingAction> policyViolationActionMap = new HashMap<>();

	private final List<NotificationContentItem> policyViolations = new LinkedList<>();
	private final List<NotificationContentItem> vulnerabilities = new LinkedList<>();

	public NotificationProcessor() {
		policyViolationTypeSet.add(PolicyViolationContentItem.class);
		policyViolationTypeSet.add(PolicyViolationClearedContentItem.class);
		policyViolationTypeSet.add(PolicyOverrideContentItem.class);
		vulnerabilityTypeSet.add(VulnerabilityContentItem.class);

		policyViolationActionMap.put(PolicyViolationContentItem.class, ProcessingAction.ADD);
		policyViolationActionMap.put(PolicyViolationClearedContentItem.class, ProcessingAction.REMOVE);
		policyViolationActionMap.put(PolicyOverrideContentItem.class, ProcessingAction.REMOVE);
	}

	public List<ProjectData> process(final SortedSet<NotificationContentItem> notifications) {
		collectNotificationGroups(notifications);
		final List<ProjectData> projectDataList = processActions();
		return projectDataList;
	}

	private void collectNotificationGroups(final SortedSet<NotificationContentItem> notifications) {
		for (final NotificationContentItem item : notifications) {
			final Class<?> key = item.getClass();
			if (policyViolationTypeSet.contains(key)) {
				policyViolations.add(item);
			} else if (vulnerabilityTypeSet.contains(key)) {
				vulnerabilities.add(item);
			} else {
				logger.info("Unknown notification seen {}", item);
			}
		}
	}

	private List<ProjectData> processActions() {
		final List<ProjectData> itemList = new LinkedList<>();
		final Map<String, NotificationContentItem> policyViolationData = processPolicyViolationActions();
		final Map<String, CategoryData> categoryData = convertMapToList(policyViolationData);
		final List<CategoryData> categoryList = new LinkedList<>();
		for (final String projectKey : categoryData.keySet()) {
			categoryList.add(categoryData.get(projectKey));
			itemList.add(new ProjectData(projectKey, categoryList));
		}
		return itemList;
	}

	private Map<String, CategoryData> convertMapToList(final Map<String, NotificationContentItem> policyViolationMap) {
		final Map<String, CategoryData> dataList = new HashMap<>();
		for (final Map.Entry<String, NotificationContentItem> entry : policyViolationMap.entrySet()) {
			final NotificationContentItem item = entry.getValue();
			final String projectKey = item.getProjectVersion().getProjectName() + " > "
					+ item.getProjectVersion().getProjectVersionName();
			final List<ItemData> itemDataList;
			if (dataList.containsKey(projectKey)) {
				final CategoryData categoryData = dataList.get(projectKey);
				itemDataList = categoryData.getItemList();
			} else {
				itemDataList = new LinkedList<>();
				dataList.put(projectKey, new CategoryData("Policy Violations", itemDataList));
			}
			final Map<String, String> dataMap = new HashMap<>();
			final PolicyViolationContentItem policyViolation = (PolicyViolationContentItem) item;
			for (final PolicyRule rule : policyViolation.getPolicyRuleList()) {
				dataMap.put(rule.getName(), "Rule");
				dataMap.put(item.getComponentName() + " " + item.getComponentVersion(), "Component:");
				itemDataList.add(new ItemData(dataMap));
			}
		}
		return dataList;
	}

	private Map<String, NotificationContentItem> processPolicyViolationActions() {
		final Map<String, NotificationContentItem> projectMap = new HashMap<>();
		for (final NotificationContentItem item : policyViolations) {
			ProcessingAction action = ProcessingAction.ADD;
			final Class<?> key = item.getClass();
			if (policyViolationActionMap.containsKey(key)) {
				action = policyViolationActionMap.get(key);
			}
			switch (action) {
			case REMOVE: {
				final String notificationKey = constructNotificationKey(item);
				projectMap.remove(notificationKey);
			}
			default:
			case ADD: {
				final String notificationKey = constructNotificationKey(item);
				projectMap.put(notificationKey, item);
				break;
			}

			}
		}
		return projectMap;
	}

	private String constructNotificationKey(final NotificationContentItem item) {
		return item.getProjectVersion().getProjectVersionLink() + item.getComponentName() + item.getComponentVersion();
	}

	private enum ProcessingAction {
		ADD, REMOVE;
	}
}
