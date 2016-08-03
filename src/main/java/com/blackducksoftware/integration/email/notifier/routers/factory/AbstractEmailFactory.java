package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.Set;

import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;

public abstract class AbstractEmailFactory {

	public final static String TOPIC_ALL = "all";

	private final EmailMessagingService emailMessagingService;

	public AbstractEmailFactory(final EmailMessagingService emailMessagingService) {
		this.emailMessagingService = emailMessagingService;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}

	public abstract Set<String> getSubscriberTopics();

	public abstract AbstractEmailRouter<?> createInstance(EmailTaskData data);
}
