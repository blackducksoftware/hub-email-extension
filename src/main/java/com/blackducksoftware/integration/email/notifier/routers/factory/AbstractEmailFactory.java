package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;

public abstract class AbstractEmailFactory {

	public final static String TOPIC_ALL = "all";

	private final EmailMessagingService emailMessagingService;
	private final CustomerProperties customerProperties;

	public AbstractEmailFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties) {
		this.emailMessagingService = emailMessagingService;
		this.customerProperties = customerProperties;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}

	public CustomerProperties getCustomerProperties() {
		return customerProperties;
	}

	public abstract Set<String> getSubscriberTopics();

	public abstract AbstractEmailRouter<?> createInstance(EmailTaskData data);
}
