package com.blackducksoftware.integration.email.mock;

import com.blackducksoftware.integration.email.messaging.AbstractPollingDispatcher;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;

public class MockDispatcher extends AbstractPollingDispatcher<String> {

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
	public RouterTaskData<String> fetchRouterConfig() {
		return new RouterTaskData<String>(TEST_DATA);
	}
}
