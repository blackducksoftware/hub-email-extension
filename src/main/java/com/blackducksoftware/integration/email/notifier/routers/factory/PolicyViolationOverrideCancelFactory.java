package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationOverrideCancelRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;

@Component
public class PolicyViolationOverrideCancelFactory extends AbstractEmailFactory<PolicyOverrideNotificationItem> {

	public PolicyViolationOverrideCancelFactory(final EmailMessagingService emailMessagingService) {
		super(emailMessagingService);
	}

	@Override
	public ItemRouter<List<PolicyOverrideNotificationItem>> createInstance(
			final RouterTaskData<List<PolicyOverrideNotificationItem>> data) {
		final PolicyViolationOverrideCancelRouter router = new PolicyViolationOverrideCancelRouter(
				getEmailMessagingService());
		router.setTaskData(data);
		return router;
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyOverrideNotificationItem.class.getName());
		return topicSet;
	}
}
