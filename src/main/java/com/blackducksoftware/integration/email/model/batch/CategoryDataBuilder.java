package com.blackducksoftware.integration.email.model.batch;

import java.util.LinkedList;
import java.util.List;

public class CategoryDataBuilder {
    private String categoryKey;

    private final List<ItemData> itemList;

    private int itemCount;

    public CategoryDataBuilder() {
        this.itemList = new LinkedList<>();
        this.itemCount = 0;
    }

    public void addItem(final ItemData item) {
        itemList.add(item);
    }

    public void removeItem(final ItemData item) {
        itemList.remove(item);
    }

    public void incrementItemCount(final int itemCount) {
        this.itemCount += itemCount;
    }

    public void decrementItemCount(final int itemCount) {
        this.itemCount -= itemCount;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(final String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(final int itemCount) {
        this.itemCount = itemCount;
    }

    public List<ItemData> getItemList() {
        return itemList;
    }

    public CategoryData build() {
        return new CategoryData(categoryKey, itemList, itemCount);
    }
}
