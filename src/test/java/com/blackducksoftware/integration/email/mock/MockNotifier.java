package com.blackducksoftware.integration.email.mock;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.notifier.AbstractNotifier;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public class MockNotifier extends AbstractNotifier {
	public static final String CRON_EXPRESSION = "0 0/1 * 1/1 * ? *";
	public final static long NOTIFIER_INTERVAL = 5000;
	private final String templateName;
	private boolean ran = false;

	public MockNotifier(final ExtensionProperties customerProperties, final NotificationDataService notificationService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService, final DataServicesFactory dataservicesFactory,
			final String templateName) {
		super(customerProperties, emailMessagingService, dataservicesFactory);
		this.templateName = templateName;
	}

	@Override
	public String getTemplateName() {
		return templateName;
	}

	@Override
	public String getNotifierPropertyKey() {
		return templateName;
	}

	@Override
	public void run() {
		ran = true;
	}

	public boolean hasRun() {
		return ran;
	}

	@Override
	public String getCronExpression() {
		return CRON_EXPRESSION;
	}
}
