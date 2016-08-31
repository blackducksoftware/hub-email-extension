package com.blackducksoftware.integration.email.model;

import java.util.Set;

public class ProjectCategory extends FreemarkerCategoryTarget<Set<VersionCategory>> {

	public ProjectCategory(final String category, final Set<VersionCategory> categoryData) {
		super(category, categoryData);
	}
}
