package com.blackducksoftware.integration.email.model.batch;

import java.util.List;

public class CategoryData {
	private final String categoryKey;
	private final List<ItemData> itemList;

	public CategoryData(final String categoryKey, final List<ItemData> itemList) {
		this.categoryKey = categoryKey;
		this.itemList = itemList;
	}

	public String getCategoryKey() {
		return categoryKey;
	}

	public List<ItemData> getItemList() {
		return itemList;
	}
}
