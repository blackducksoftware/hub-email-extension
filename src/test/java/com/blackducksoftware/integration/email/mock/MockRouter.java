package com.blackducksoftware.integration.email.mock;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.NotificationDataService;

public class MockRouter extends AbstractRouter {
	public final static long ROUTER_INTERVAL = 5000;
	private final String templateName;
	private boolean ran = false;

	public MockRouter(final CustomerProperties customerProperties, final NotificationDataService notificationService,
			final UserRestService userRestService, final EmailMessagingService emailMessagingService,
			final String templateName) {
		super(customerProperties, notificationService, userRestService, emailMessagingService);
		this.templateName = templateName;
	}

	@Override
	public String getRouterKey() {
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
}
