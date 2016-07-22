package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.email.messaging.AbstractPollingConsumer;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

public class NotificationConsumer extends AbstractPollingConsumer<NotificationItem> {

	@Override
	public List<NotificationItem> fetchMessages() {
		final List<NotificationItem> messages = new ArrayList<NotificationItem>();

		return messages;
	}
}
