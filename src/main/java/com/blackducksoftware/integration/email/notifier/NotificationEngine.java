package com.blackducksoftware.integration.email.notifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;

@Component
public class NotificationEngine {

	@Autowired
	private NotificationDispatcher consumer;
	@Autowired
	private NotificationRouter router;

	@PostConstruct
	public void configure() {
		final EmailSystemConfiguration emailSystemConfiguration = new EmailSystemConfiguration();
		consumer.addListener(router);
		router.configure(emailSystemConfiguration);
	}

	public void start() {
		consumer.start();
	}

	@PreDestroy
	public void stop() {
		consumer.stop();
	}
}
