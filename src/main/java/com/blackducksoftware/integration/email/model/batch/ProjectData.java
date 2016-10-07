package com.blackducksoftware.integration.email.model.batch;

import java.util.List;

public class ProjectData {
	private final String projectKey;
	private final List<CategoryData> categoryList;

	public ProjectData(final String projectKey, final List<CategoryData> categoryList) {
		this.projectKey = projectKey;
		this.categoryList = categoryList;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public List<CategoryData> getCategoryList() {
		return categoryList;
	}
}
