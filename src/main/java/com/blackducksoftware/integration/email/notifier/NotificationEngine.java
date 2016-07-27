package com.blackducksoftware.integration.email.notifier;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class NotificationEngine {
	private static Logger logger = LoggerFactory.getLogger(NotificationEngine.class);

	@Autowired
	private RouterConfigDispatcher configDispatcher;

	@Autowired
	private NotificationDispatcher notificationDispatcher;

	@Autowired
	private ItemRouter<EmailSystemConfiguration, List<NotificationItem>, EmailMessage>[] routerArray;

	@PostConstruct
	public void configure() {
		if (routerArray != null) {
			for (final ItemRouter<EmailSystemConfiguration, List<NotificationItem>, EmailMessage> router : routerArray) {
				notificationDispatcher.addListener(router);
				configDispatcher.addListener(router);
			}
		}
	}

	@PostConstruct
	public void start() {
		logger.info("Starting notification engine.");
		configDispatcher.start();
		notificationDispatcher.start();
	}

	@PreDestroy
	public void stop() {
		logger.info("Stopping notification engine.");
		notificationDispatcher.stop();
		configDispatcher.stop();
	}
}
