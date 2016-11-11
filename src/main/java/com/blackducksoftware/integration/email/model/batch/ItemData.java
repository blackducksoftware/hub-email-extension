package com.blackducksoftware.integration.email.model.batch;

import java.util.Set;

public class ItemData {
    private final Set<ItemEntry> dataSet;

    public ItemData(final Set<ItemEntry> dataMap) {
        this.dataSet = dataMap;
    }

    public Set<ItemEntry> getDataSet() {
        return dataSet;
    }

    @Override
    public String toString() {
        return "ItemData [dataSet=" + dataSet + "]";
    }
}
