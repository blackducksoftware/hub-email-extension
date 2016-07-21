package com.blackducksoftware.integration.email.notifier;

import com.blackducksoftware.integration.email.model.EmailConfiguration;

public class NotificationEngine {

	private final NotificationConsumer consumer;
	private final NotificationRouter router;

	public NotificationEngine() {
		consumer = new NotificationConsumer();
		router = new NotificationRouter();
	}

	public void configure() {
		final EmailConfiguration config = new EmailConfiguration();
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
