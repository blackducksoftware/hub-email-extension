package com.blackducksoftware.integration.email.notifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;

@Component
public class NotificationEngine {
	private static Logger logger = LoggerFactory.getLogger(NotificationEngine.class);
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

	@PostConstruct
	public void start() {
		logger.info("Starting notification engine.");
		consumer.start();
	}

	@PreDestroy
	public void stop() {
		logger.info("Stopping notification engine.");
		consumer.stop();
	}
}
