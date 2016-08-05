package com.blackducksoftware.integration.email.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.notifier.AbstractPollingDispatcher;
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
	public Map<String, EmailTaskData> fetchData() {
		final Map<String, EmailTaskData> map = new HashMap<>();
		final List<Object> data = new ArrayList<>();
		data.add(TEST_DATA);
		map.put(MockRouterFactory.TOPIC_KEY, new EmailTaskData(data));
		return map;
	}

}
