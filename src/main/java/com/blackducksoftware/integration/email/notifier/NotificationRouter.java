package com.blackducksoftware.integration.email.notifier;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.email.dto.ConfigResponse;
import com.blackducksoftware.integration.email.notifier.rules.AbstractNotificationFilter;
import com.blackducksoftware.integration.email.notifier.rules.PolicyViolationRule;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.messaging.AbstractMessageRouter;

public class NotificationRouter extends AbstractMessageRouter<ConfigResponse, NotificationItem> {

	private final Map<String, AbstractNotificationFilter<?>> filterMap = new HashMap<>();

	public NotificationRouter() {
		filterMap.put(PolicyViolationRule.class.getName(), new PolicyViolationRule());
	}

	@Override
	public void route(final NotificationItem message) {
		final String typeName = message.getClass().getName();
		if (filterMap.containsKey(typeName)) {
			final AbstractNotificationFilter<?> filter = filterMap.get(typeName);
			filter.apply(message);
		}
	}

	@Override
	public void configure(final ConfigResponse config) {

	}
}
