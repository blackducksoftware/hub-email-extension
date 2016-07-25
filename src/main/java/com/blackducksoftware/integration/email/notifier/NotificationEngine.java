package com.blackducksoftware.integration.email.notifier;

import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;

public class NotificationEngine {

	private final NotificationConsumer consumer;
	private final NotificationRouter router;

	public NotificationEngine() {
		consumer = new NotificationConsumer();
		router = new NotificationRouter();
	}

	public void configure() {
		final EmailSystemConfiguration emailSystemConfiguration = new EmailSystemConfiguration();
		consumer.addListener(router);
		router.configure(emailSystemConfiguration);
	}

	public void start() {
		consumer.start();
	}

	public void stop() {
		consumer.stop();
	}
}
