package com.blackducksoftware.integration.email.mock;

import static org.junit.Assert.assertEquals;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;

public class MockRouter extends ItemRouter<String> {

	public final static String ROUTER_NAME = "Mock Router";
	private final String expectedData;

	public MockRouter(final RouterTaskData<String> data, final String expectedData) {
		super(data);
		this.expectedData = expectedData;
	}

	@Override
	public void execute(final RouterTaskData<String> taskData) {
		assertEquals(expectedData, taskData.getData());
	}

	@Override
	public String getName() {
		return ROUTER_NAME;
	}
}
