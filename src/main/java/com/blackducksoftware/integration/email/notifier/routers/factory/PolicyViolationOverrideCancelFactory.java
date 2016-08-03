package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationOverrideCancelRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;

public class PolicyViolationOverrideCancelFactory extends AbstractEmailFactory {

	public PolicyViolationOverrideCancelFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties) {
		super(emailMessagingService, customerProperties);
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		final PolicyViolationOverrideCancelRouter router = new PolicyViolationOverrideCancelRouter(
				getEmailMessagingService(), getCustomerProperties(), data);
		return router;
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyOverrideNotificationItem.class.getName());
		return topicSet;
	}
}
