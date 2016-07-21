package com.blackducksoftware.integration.email.notifier;

import com.blackducksoftware.integration.email.dto.ConfigResponse;

public class NotificationEngine {

	private final NotificationConsumer consumer;
	private final NotificationRouter router;

	public NotificationEngine() {
		consumer = new NotificationConsumer();
		router = new NotificationRouter();
	}

	public void configure() {
		final ConfigResponse config = new ConfigResponse();
		consumer.addListener(router);
		router.configure(config);
	}

	public void start() {
		consumer.start();
	}

	public void stop() {
		consumer.stop();
	}
}
