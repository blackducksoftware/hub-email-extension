package com.blackducksoftware.integration.email.model;

public class FreemarkerCategoryTarget<T> {

	private final String category;
	private final T categoryData;

	public FreemarkerCategoryTarget(final String category, final T categoryData) {
		this.category = category;
		this.categoryData = categoryData;
	}

	public String getCategory() {
		return category;
	}

	public T getCategoryData() {
		return categoryData;
	}
}
