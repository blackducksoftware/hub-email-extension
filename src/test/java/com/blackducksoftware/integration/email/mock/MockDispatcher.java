package com.blackducksoftware.integration.email.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.notifier.AbstractPollingDispatcher;
import com.blackducksoftware.integration.email.notifier.routers.EmailContentItem;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;

public class MockDispatcher extends AbstractPollingDispatcher {
	public final static String NAME = "Mock Dispatcher";
	public final static int INTERVAL = 500;
	public final static int DELAY = 0;
	public final static String TEST_DATA = "Test data string";

	@Override
	public void init() {
		setName(NAME);
		setInterval(INTERVAL);
		setStartupDelay(DELAY);
	}

	@Override
	public List<EmailContentItem> fetchData() {
		final List<EmailContentItem> data = new ArrayList<>();
		data.add(new EmailContentItem(TEST_DATA, TEST_DATA, TEST_DATA, TEST_DATA));

		return data;
	}

	@Override
	public Map<String, List<Object>> partitionData(final List<EmailContentItem> dataList) {
		final HashMap<String, List<Object>> map = new HashMap<>();
		final List<Object> data = new ArrayList<>();
		data.addAll(dataList);
		map.put(MockRouterFactory.TOPIC_KEY, data);
		return map;
	}

	@Override
	public Map<String, EmailTaskData> filterData(final Map<String, List<Object>> partitionedData) {
		final Map<String, EmailTaskData> templateDataMap = new HashMap<>();
		final Set<String> topicSet = partitionedData.keySet();
		for (final String topic : topicSet) {
			templateDataMap.put(topic, new EmailTaskData(partitionedData.get(topic)));
		}
		return templateDataMap;
	}

}
