package com.blackducksoftware.integration.email.notifier;

import java.util.TimerTask;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;

public abstract class AbstractNotifier extends TimerTask {
	private final ExtensionProperties customerProperties;
	private final EmailMessagingService emailMessagingService;
	private final DataServicesFactory dataServicesFactory;
	private String hubExtensionUri;

	public AbstractNotifier(final ExtensionProperties customerProperties,
			final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory) {
		this.customerProperties = customerProperties;
		this.emailMessagingService = emailMessagingService;
		this.dataServicesFactory = dataServicesFactory;
	}

	public ExtensionProperties getCustomerProperties() {
		return customerProperties;
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
