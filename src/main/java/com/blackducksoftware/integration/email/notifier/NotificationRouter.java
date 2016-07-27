package com.blackducksoftware.integration.email.notifier;

<<<<<<< HEAD
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.events.MessageEvent;
import com.blackducksoftware.integration.email.messaging.events.MessageEventListener;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class NotificationRouter implements MessageEventListener<NotificationItem> {
	@Override
	public void receive(final MessageEvent<NotificationItem> event) {
=======
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class NotificationRouter extends AbstractEmailRouter {

	private final static Logger logger = LoggerFactory.getLogger(NotificationRouter.class);
>>>>>>> 9a97f0dbd5e371f5dfdc31e27d9518dd1a58f177

	@Override
	public void configure(final EmailSystemConfiguration data) {
		logger.info("Configuration data event received: " + data);
	}

<<<<<<< HEAD
	public void configure(final EmailSystemProperties emailSystemProperties) {

	}

	public void send(final MessageEvent<Map<String, Object>> event) {
=======
	@Override
	public void receive(final List<NotificationItem> data) {
		logger.info("Received notification data event received: " + data);
	}

	@Override
	public void send(final EmailMessage data) {
>>>>>>> 9a97f0dbd5e371f5dfdc31e27d9518dd1a58f177

	}

}
