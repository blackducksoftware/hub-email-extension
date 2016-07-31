package com.blackducksoftware.integration.email.mock;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.ItemRouterFactory;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;

public class MockRouterFactory extends ItemRouterFactory<String> {

	private final String expectedData;

	public MockRouterFactory(final String expectedData) {
		this.expectedData = expectedData;
	}

	@Override
	public ItemRouter<String> createInstance(final RouterTaskData<String> data) {
		return new MockRouter(data, expectedData);
	}

}
