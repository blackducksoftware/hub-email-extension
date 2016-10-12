package com.blackducksoftware.integration.email.notifier;

import java.util.TimerTask;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public abstract class AbstractNotifier extends TimerTask {
	private final ExtensionProperties customerProperties;
	private final NotificationDataService notificationDataService;
	private final ExtensionConfigDataService extensionConfigDataService;
	private final EmailMessagingService emailMessagingService;
	private final DataServicesFactory dataServicesFactory;
	private String hubExtensionUri;

	public AbstractNotifier(final ExtensionProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory) {
		this.customerProperties = customerProperties;
		this.notificationDataService = notificationDataService;
		this.extensionConfigDataService = extensionConfigDataService;
		this.emailMessagingService = emailMessagingService;
		this.dataServicesFactory = dataServicesFactory;
	}

	public ExtensionProperties getCustomerProperties() {
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

	public DataServicesFactory getDataServicesFactory() {
		return dataServicesFactory;
	}

	public String getName() {
		return getClass().getName();
	}

	public String getHubExtensionUri() {
		return hubExtensionUri;
	}

	public void setHubExtensionUri(final String hubExtensionUri) {
		this.hubExtensionUri = hubExtensionUri;
	}

	public abstract String getTemplateName();

	public abstract String getCronExpression();

	public abstract String getNotifierPropertyKey();

	public long getStartDelayMilliseconds() {
		return 0;
	}
}
