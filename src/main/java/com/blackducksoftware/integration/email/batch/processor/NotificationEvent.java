package com.blackducksoftware.integration.email.batch.processor;

import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;

public class NotificationEvent {
	private final ProcessingAction action;
	private final String projectName;
	private final String projectVersion;
	private final String componentName;
	private final String componentVersion;
	private final NotificationCategoryEnum categoryType;
	private final Set<ItemEntry> dataSet;
	private final String eventKey;
	private final String projectKey;
	private final Set<String> vulnerabilityIdSet;

	public NotificationEvent(final ProcessingAction action, final String projectName, final String projectVersion,
			final String componentName, final String componentVersion, final String eventKey,
			final NotificationCategoryEnum categoryType, final Set<ItemEntry> dataSet,
			final Set<String> vulnerabilityIdSet) {
		this.action = action;
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.componentName = componentName;
		this.componentVersion = componentVersion;
		this.categoryType = categoryType;
		this.dataSet = dataSet;
		this.vulnerabilityIdSet = vulnerabilityIdSet;
		this.projectKey = projectName + projectVersion;
		this.eventKey = eventKey;
	}

	public ProcessingAction getAction() {
		return action;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getComponentVersion() {
		return componentVersion;
	}

	public NotificationCategoryEnum getCategoryType() {
		return categoryType;
	}

	public Set<ItemEntry> getDataSet() {
		return dataSet;
	}

	public Set<String> getVulnerabilityIdSet() {
		return vulnerabilityIdSet;
	}

	public String getEventKey() {
		return eventKey;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public int getCategoryItemCount() {
		if (vulnerabilityIdSet != null && vulnerabilityIdSet.isEmpty()) {
			return 1;
		} else {
			return vulnerabilityIdSet.size();
		}
	}
}
