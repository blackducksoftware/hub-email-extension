package com.blackducksoftware.integration.email.model.batch;

import java.util.LinkedList;
import java.util.List;

public class ProjectDataBuilder {

	public String projectKey;
	public List<CategoryData> categoryList;

	private ProjectDataBuilder() {
		projectKey = "";
		categoryList = new LinkedList<>(); // preserve insertion order
	}

	private ProjectDataBuilder(final String projectKey, final List<CategoryData> categoryMap) {
		this.projectKey = projectKey;
		this.categoryList = categoryMap;
	}

	public void applyProjectKey(final String projectKey) {
		this.projectKey = projectKey;
	}

	public boolean addCategory(final CategoryData categoryData) {
		return categoryList.add(categoryData);
	}

	public boolean removeCategory(final CategoryData categoryData) {
		return categoryList.remove(categoryData);
	}

	public ProjectData build() {
		return new ProjectData(projectKey, categoryList);
	}
}
