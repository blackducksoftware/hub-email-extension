package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.List;

import com.blackducksoftware.integration.email.messaging.ItemRouterFactory;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

public abstract class AbstractEmailFactory<T extends NotificationItem> extends ItemRouterFactory<List<T>> {

	public final static String TOPIC_ALL = "all";

	private final EmailMessagingService emailMessagingService;

	public AbstractEmailFactory(final EmailMessagingService emailMessagingService) {
		this.emailMessagingService = emailMessagingService;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}
}
