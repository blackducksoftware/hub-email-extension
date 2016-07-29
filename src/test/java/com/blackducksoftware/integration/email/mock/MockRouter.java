package com.blackducksoftware.integration.email.mock;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import com.blackducksoftware.integration.email.messaging.ItemRouter;

public class MockRouter extends ItemRouter<String, String, String> {

	private final Set<String> topicSet;
	private final String expectedConfig;
	private final String expectedReceive;
	private final String expectedSend;

	public MockRouter(final Set<String> topicSet, final String expectedConfig, final String expectedRecieve,
			final String expectedSend) {
		this.topicSet = topicSet;
		this.expectedConfig = expectedConfig;
		this.expectedReceive = expectedRecieve;
		this.expectedSend = expectedSend;
	}

	@Override
	public Set<String> getTopics() {
		return topicSet;
	}

	@Override
	public void configure(final String data) {
		assertEquals(expectedConfig, data);
	}

	@Override
	public void receive(final String data) {
		assertEquals(expectedReceive, data);
	}

	@Override
	public void send(final String data) {
		assertEquals(expectedSend, data);
	}
}
