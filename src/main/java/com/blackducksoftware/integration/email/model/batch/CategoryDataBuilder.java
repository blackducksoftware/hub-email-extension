package com.blackducksoftware.integration.email.model.batch;

import java.util.LinkedList;
import java.util.List;

public class CategoryDataBuilder {
	private String categoryKey;
	private final List<ItemData> itemList;

	private CategoryDataBuilder() {
		categoryKey = "";
		itemList = new LinkedList<>();
	}

	public void applyKey(final String key) {
		this.categoryKey = key;
	}

	public boolean addItem(final ItemData itemData) {
		return itemList.add(itemData);
	}

	public CategoryData build() {
		return new CategoryData(categoryKey, itemList);
	}
}
