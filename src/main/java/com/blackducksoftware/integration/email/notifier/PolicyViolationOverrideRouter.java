package com.blackducksoftware.integration.email.notifier;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class PolicyViolationOverrideRouter extends AbstractEmailRouter {

	private final static Logger logger = LoggerFactory.getLogger(PolicyViolationOverrideRouter.class);

	@Override
	public void configure(final EmailSystemConfiguration data) {
		logger.info("Configuration data event received for " + getClass().getName() + ": " + data);
	}

	@Override
	public void receive(final List<NotificationItem> data) {
		logger.info("Received notification data event received for " + getClass().getName() + ": " + data);
	}

	@Override
	public void send(final EmailMessage data) {
		// TODO Auto-generated method stub

	}

}
