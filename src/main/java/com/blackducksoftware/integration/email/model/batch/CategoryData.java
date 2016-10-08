package com.blackducksoftware.integration.email.model.batch;

import java.util.List;

public class CategoryData {
	private final String projectName;
	private final String projectVersion;
	private final String projectKey;
	private final String categoryKey;
	private final List<ItemData> itemList;

	public CategoryData(final String projectName, final String projectVersion, final String categoryKey,
			final List<ItemData> itemList) {
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.projectKey = projectName + projectVersion;
		this.categoryKey = categoryKey;
		this.itemList = itemList;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getCategoryKey() {
		return categoryKey;
	}

	public List<ItemData> getItemList() {
		return itemList;
	}
}
