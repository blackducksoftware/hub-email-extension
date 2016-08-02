package com.blackducksoftware.integration.email.mock;

import java.util.HashSet;
import java.util.Set;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.ItemRouterFactory;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;

public class MockRouterFactory extends ItemRouterFactory<String> {

	public final static String TOPIC_KEY = "MockTopic";
	private final String expectedData;

	public MockRouterFactory(final String expectedData) {
		this.expectedData = expectedData;
	}

	@Override
	public ItemRouter<String> createInstance(final RouterTaskData<String> data) {
		return new MockRouter(expectedData);
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> subscriberSet = new HashSet<>();
		subscriberSet.add(TOPIC_KEY);
		return subscriberSet;
	}

}
