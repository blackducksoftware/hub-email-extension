package com.blackducksoftware.integration.email.notifier.routers;

import java.util.TimerTask;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public abstract class AbstractRouter extends TimerTask {
	private final CustomerProperties customerProperties;
	private final NotificationDataService notificationDataService;
	private final ExtensionConfigDataService extensionConfigDataService;
	private final EmailMessagingService emailMessagingService;
	private String hubExtensionId;

	public AbstractRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService) {
		this.customerProperties = customerProperties;
		this.notificationDataService = notificationDataService;
		this.extensionConfigDataService = extensionConfigDataService;
		this.emailMessagingService = emailMessagingService;
	}

	public CustomerProperties getCustomerProperties() {
		return customerProperties;
	}

	public NotificationDataService getNotificationDataService() {
		return notificationDataService;
	}

	public ExtensionConfigDataService getExtensionConfigDataService() {
		return extensionConfigDataService;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}

	public String getName() {
		return getClass().getName();
	}

	public String getHubExtensionId() {
		return hubExtensionId;
	}

	public void setHubExtensionId(final String hubExtensionId) {
		this.hubExtensionId = hubExtensionId;
	}

	public abstract String getTemplateName();

	public abstract long getIntervalMilliseconds();

	public abstract String getRouterPropertyKey();

	public long getStartDelayMilliseconds() {
		return 0;
	}
}
