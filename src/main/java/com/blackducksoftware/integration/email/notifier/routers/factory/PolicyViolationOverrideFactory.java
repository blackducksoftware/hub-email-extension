package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationOverrideRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.NotificationService;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;

public class PolicyViolationOverrideFactory extends AbstractEmailFactory {
	public PolicyViolationOverrideFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService) {
		super(emailMessagingService, customerProperties, notificationService);
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		final PolicyViolationOverrideRouter router = new PolicyViolationOverrideRouter(getEmailMessagingService(),
				getCustomerProperties(), getNotificationService(), data);
		return router;
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyOverrideNotificationItem.class.getName());
		return topicSet;
	}

}
