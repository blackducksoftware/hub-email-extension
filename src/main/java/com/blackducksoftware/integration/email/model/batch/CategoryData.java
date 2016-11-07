package com.blackducksoftware.integration.email.model.batch;

import java.util.List;

public class CategoryData {
    private final String categoryKey;

    private final List<ItemData> itemList;

    private final int itemCount;

    public CategoryData(final String categoryKey, final List<ItemData> itemList, final int itemCount) {
        this.categoryKey = categoryKey;
        this.itemList = itemList;
        this.itemCount = itemCount;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public List<ItemData> getItemList() {
        return itemList;
    }

    public int getItemCount() {
        return itemCount;
    }

    @Override
    public String toString() {
        return "CategoryData [categoryKey=" + categoryKey + ", itemList=" + itemList + ", itemCount=" + itemCount + "]";
    }
}
