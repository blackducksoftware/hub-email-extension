package com.blackducksoftware.integration.email.model;

import java.util.Map;

public class VersionCategory extends FreemarkerCategoryTarget<Map<String, String>> {

	public VersionCategory(final String category, final Map<String, String> categoryData) {
		super(category, categoryData);
	}
}
