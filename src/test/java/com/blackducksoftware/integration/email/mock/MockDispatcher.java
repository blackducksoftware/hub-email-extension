package com.blackducksoftware.integration.email.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.messaging.AbstractPollingDispatcher;

public class MockDispatcher extends AbstractPollingDispatcher<String, MockRouter> {

	public final static String NAME = "Mock Dispatcher";
	public final static int INTERVAL = 500;
	public final static int DELAY = 0;

	private final Set<String> topics;
	private final boolean receiveTask;
	private final String topicData;

	public MockDispatcher(final Set<String> topics, final boolean receiveTask, final String topicData) {
		this.topics = topics;
		this.receiveTask = receiveTask;
		this.topicData = topicData;
	}

	@Override
	public void init() {
		setName(NAME);
		setInterval(INTERVAL);
		setStartupDelay(DELAY);
	}

	@Override
	public Map<String, String> fetchData() {
		final Map<String, String> data = new HashMap<>();
		for (final String topic : topics) {
			data.put(topic, topicData);
		}
		return data;
	}

	@Override
	public Runnable createEventTask(final MockRouter router, final String data) {
		if (receiveTask) {
			return new ReceiveTask(router, data);
		} else {
			return new ConfigureTask(router, data);
		}
	}

	private class ReceiveTask implements Runnable {

		private final MockRouter router;
		private final String data;

		public ReceiveTask(final MockRouter router, final String data) {
			this.router = router;
			this.data = data;
		}

		@Override
		public void run() {
			router.receive(data);
		}
	}

	private class ConfigureTask implements Runnable {

		private final MockRouter router;
		private final String data;

		public ConfigureTask(final MockRouter router, final String data) {
			this.router = router;
			this.data = data;
		}

		@Override
		public void run() {
			router.configure(data);
		}
	}
}
