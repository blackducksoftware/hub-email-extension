package com.blackducksoftware.integration.email.model.batch;

import java.util.Map;

import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;

public class ProjectData {
	private final String projectKey;
	private final String projectName;
	private final String projectVersion;
	private final Map<NotificationCategoryEnum, CategoryData> categoryMap;

	public ProjectData(final String projectName, final String projectVersion,
			final Map<NotificationCategoryEnum, CategoryData> categoryMap) {
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.categoryMap = categoryMap;
		this.projectKey = projectName + projectVersion;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public Map<NotificationCategoryEnum, CategoryData> getCategoryMap() {
		return categoryMap;
	}
}
