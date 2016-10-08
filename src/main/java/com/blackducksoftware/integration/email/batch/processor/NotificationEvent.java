package com.blackducksoftware.integration.email.batch.processor;

import java.util.Map;

public class NotificationEvent {
	private final ProcessingAction action;
	private final String projectName;
	private final String projectVersion;
	private final String componentName;
	private final String componentVersion;
	private final String categoryType;
	private final Map<String, String> dataMap;
	private final String eventKey;

	public NotificationEvent(final ProcessingAction action, final String projectName, final String projectVersion,
			final String componentName, final String componentVersion, final String categoryType,
			final Map<String, String> dataMap) {
		this.action = action;
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.componentName = componentName;
		this.componentVersion = componentVersion;
		this.categoryType = categoryType;
		this.dataMap = dataMap;
		this.eventKey = projectName + projectVersion + componentName + componentVersion;
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

	public String getCategoryType() {
		return categoryType;
	}

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public String getEventKey() {
		return eventKey;
	}
}
