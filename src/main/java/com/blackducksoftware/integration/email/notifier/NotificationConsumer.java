package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.messaging.AbstractPollingConsumer;

public class NotificationConsumer extends AbstractPollingConsumer<NotificationItem> {

	@Override
	public List<NotificationItem> fetchMessages() {
		final List<NotificationItem> messages = new ArrayList<NotificationItem>();

		return messages;
	}
}
