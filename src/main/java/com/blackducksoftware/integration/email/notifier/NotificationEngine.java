package com.blackducksoftware.integration.email.notifier;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouterFactory;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class NotificationEngine {
	private final Logger logger = LoggerFactory.getLogger(NotificationEngine.class);

	@Autowired
	private NotificationDispatcher notificationDispatcher;

	@Autowired
	private ItemRouterFactory<List<? extends NotificationItem>>[] routerArray;

	public void configure() {
		if (routerArray != null) {
			final List<ItemRouterFactory<List<? extends NotificationItem>>> routerList = Arrays.asList(routerArray);
			notificationDispatcher.attachRouters(routerList);
		}
	}

	@PostConstruct
	public void start() {
		logger.info("Starting notification engine.");
		configure();
		notificationDispatcher.start();
	}

	@PreDestroy
	public void stop() {
		logger.info("Stopping notification engine.");
		notificationDispatcher.stop();
	}
}
