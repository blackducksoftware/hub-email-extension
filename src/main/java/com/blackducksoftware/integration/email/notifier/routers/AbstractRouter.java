package com.blackducksoftware.integration.email.notifier.routers;

import java.util.TimerTask;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.NotificationDataService;

public abstract class AbstractRouter extends TimerTask {

	private final CustomerProperties customerProperties;
	private final NotificationDataService notificationDataService;
	private final UserRestService userRestService;
	private final EmailMessagingService emailMessagingService;

	public AbstractRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		this.customerProperties = customerProperties;
		this.notificationDataService = notificationDataService;
		this.userRestService = userRestService;
		this.emailMessagingService = emailMessagingService;
	}

	public CustomerProperties getCustomerProperties() {
		return customerProperties;
	}

	public NotificationDataService getNotificationDataService() {
		return notificationDataService;
	}

	public UserRestService getUserRestService() {
		return userRestService;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}

	public String getName() {
		return getClass().getName();
	}

	public abstract String getTemplateName();

	public abstract long getIntervalMilliseconds();

	public long getStartDelayMilliseconds() {
		return 0;
	}
}
