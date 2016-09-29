package com.blackducksoftware.integration.email.mock;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public class MockRouter extends AbstractRouter {
	public final static long ROUTER_INTERVAL = 5000;
	private final String templateName;
	private boolean ran = false;

	public MockRouter(final CustomerProperties customerProperties, final NotificationDataService notificationService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService, final String templateName) {
		super(customerProperties, notificationService, extensionConfigDataService, emailMessagingService);
		this.templateName = templateName;
	}

	@Override
	public String getTemplateName() {
		return templateName;
	}

	@Override
	public String getRouterPropertyKey() {
		return templateName;
	}

	@Override
	public long getIntervalMilliseconds() {
		return ROUTER_INTERVAL;
	}

	@Override
	public void run() {
		ran = true;
	}

	public boolean hasRun() {
		return ran;
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		run();
	}
}
