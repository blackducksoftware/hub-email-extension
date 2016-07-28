package com.blackducksoftware.integration.email.notifier;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;

@Component
public class PolicyViolationRouter extends AbstractEmailRouter {

	private final static Logger logger = LoggerFactory.getLogger(PolicyViolationRouter.class);

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

	@Override
	public Set<String> getConfigureEventTopics() {
		final Set<String> topics = new LinkedHashSet<>();
		topics.add("emailconfigtopic");
		return topics;
	}

	@Override
	public Set<String> getReceiveEventTopics() {
		final Set<String> topics = new LinkedHashSet<>();
		topics.add(RuleViolationNotificationItem.class.getName());
		return topics;
	}

}
