package com.blackducksoftware.integration.email.notifier.rules;

import com.blackducksoftware.integration.email.dto.ConfigResponse;
import com.blackducksoftware.integration.hub.api.notification.RuleViolationNotificationItem;

public class PolicyViolationRule extends AbstractNotificationFilter<RuleViolationNotificationItem> {

	@Override
	public void route(final RuleViolationNotificationItem message) {
		// determine if we should send it
	}

	@Override
	public void configure(final ConfigResponse config) {
	}
}
