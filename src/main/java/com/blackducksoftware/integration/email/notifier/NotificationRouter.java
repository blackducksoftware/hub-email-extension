package com.blackducksoftware.integration.email.notifier;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.events.MessageEvent;
import com.blackducksoftware.integration.email.messaging.events.MessageEventListener;
import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class NotificationRouter implements MessageEventListener<NotificationItem> {

	@Override
	public void receive(final MessageEvent<NotificationItem> event) {

	}

	public void configure(final EmailSystemConfiguration emailSystemConfiguration) {

	}

	public void send(final MessageEvent<EmailMessage> event) {

	}
}
