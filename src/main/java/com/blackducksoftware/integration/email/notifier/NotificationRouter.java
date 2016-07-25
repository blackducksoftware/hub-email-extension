package com.blackducksoftware.integration.email.notifier;

import com.blackducksoftware.integration.email.messaging.events.MessageEvent;
import com.blackducksoftware.integration.email.messaging.events.MessageEventListener;
import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

public class NotificationRouter implements MessageEventListener<NotificationItem> {

	@Override
	public void receive(final MessageEvent<NotificationItem> event) {

	}

	public void configure(final EmailSystemConfiguration emailSystemConfiguration) {

	}

	public void send(final MessageEvent<EmailMessage> event) {

	}
}
