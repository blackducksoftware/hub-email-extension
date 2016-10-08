package com.blackducksoftware.integration.email.model.batch;

import java.util.List;

public class ProjectData {
	private final String projectKey;
	private final String projectName;
	private final String projectVersion;
	private final List<CategoryData> categoryList;

	public ProjectData(final String projectName, final String projectVersion, final List<CategoryData> categoryList) {
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.categoryList = categoryList;
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

	public List<CategoryData> getCategoryList() {
		return categoryList;
	}
}
