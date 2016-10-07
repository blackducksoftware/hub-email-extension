package com.blackducksoftware.integration.email.model.batch;

import java.util.Map;

public class ItemData {
	private final Map<String, String> dataMap;

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public ItemData(final Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}
}
