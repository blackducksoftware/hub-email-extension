package com.blackducksoftware.integration.email.batch.processor;

import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;

public class NotificationEvent {
	private final ProcessingAction action;
	private final String projectName;
	private final String projectVersion;
	private final String componentName;
	private final String componentVersion;
	private final NotificationCategory categoryType;
	private final Set<ItemEntry> dataSet;
	private final String eventKey;
	private final Set<String> vulnerabilityIdSet;

	public NotificationEvent(final ProcessingAction action, final String projectName, final String projectVersion,
			final String componentName, final String componentVersion, final NotificationCategory categoryType,
			final Set<ItemEntry> dataSet, final Set<String> vulnerabilityIdSet) {
		this.action = action;
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.componentName = componentName;
		this.componentVersion = componentVersion;
		this.categoryType = categoryType;
		this.dataSet = dataSet;
		this.vulnerabilityIdSet = vulnerabilityIdSet;
		this.eventKey = projectName + projectVersion + componentName + componentVersion + categoryType.name();
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

	public NotificationCategory getCategoryType() {
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
}
