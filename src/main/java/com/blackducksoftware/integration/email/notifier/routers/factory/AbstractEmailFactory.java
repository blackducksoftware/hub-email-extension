package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.AbstractTransform;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public abstract class AbstractEmailFactory {
	public final static String TOPIC_ALL = "all";

	private final EmailMessagingService emailMessagingService;
	private final CustomerProperties customerProperties;
	private final NotificationService notificationService;
	private final Map<String, AbstractTransform> transformMap;

	public AbstractEmailFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractTransform> transformMap) {
		this.emailMessagingService = emailMessagingService;
		this.customerProperties = customerProperties;
		this.notificationService = notificationService;
		this.transformMap = transformMap;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}

	public CustomerProperties getCustomerProperties() {
		return customerProperties;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public Map<String, AbstractTransform> getTransformMap() {
		return transformMap;
	}

	public abstract Set<String> getSubscriberTopics();

	public abstract AbstractEmailRouter<?> createInstance(EmailTaskData data);

}
