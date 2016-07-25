package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.AbstractPollingDispatcher;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class NotificationDispatcher extends AbstractPollingDispatcher<NotificationItem> {

	@Override
	public List<NotificationItem> fetchMessages() {
		final List<NotificationItem> messages = new ArrayList<NotificationItem>();

		return messages;
	}
}
