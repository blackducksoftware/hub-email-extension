package com.blackducksoftware.integration.email.notifier.routers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;

@Component
public class PolicyViolationOverrideRouter extends AbstractEmailRouter<PolicyOverrideNotificationItem> {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationOverrideRouter.class);

	@Override
	public void configure(final EmailSystemProperties data) {
		logger.info("Configuration data event received for " + getClass().getName() + ": " + data);
	}

	@Override
	public void receive(final List<PolicyOverrideNotificationItem> data) {
		logger.info("PolicyOverrideNotificationItem received: " + (data == null ? 0 : data.size()));
	}

	@Override
	public void send(final Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getTopics() {
		final Set<String> topics = new LinkedHashSet<>();
		topics.add(PolicyOverrideNotificationItem.class.getName());
		return topics;
	}
}
