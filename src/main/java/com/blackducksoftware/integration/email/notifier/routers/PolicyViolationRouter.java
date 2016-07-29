package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;

@Component
public class PolicyViolationRouter extends AbstractEmailRouter<RuleViolationNotificationItem> {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationRouter.class);

	@Override
	public void configure(final EmailSystemProperties data) {
		logger.info("Configuration data event received for " + getClass().getName() + ": " + data);
	}

	@Override
	public Set<String> getTopics() {
		final Set<String> topics = new LinkedHashSet<>();
		topics.add(RuleViolationNotificationItem.class.getName());
		return topics;
	}

	@Override
	public EmailData transform(final List<RuleViolationNotificationItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> emailDataMap = new HashMap<>();
		return new EmailData(addresses, emailDataMap);
	}
}
