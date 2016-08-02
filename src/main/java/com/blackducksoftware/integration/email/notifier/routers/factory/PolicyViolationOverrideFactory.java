package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationOverrideRouter;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;

@Component
public class PolicyViolationOverrideFactory extends AbstractEmailFactory<PolicyOverrideNotificationItem> {

	@Override
	public ItemRouter<List<PolicyOverrideNotificationItem>> createInstance(
			final RouterTaskData<List<PolicyOverrideNotificationItem>> data) {
		final PolicyViolationOverrideRouter router = new PolicyViolationOverrideRouter();
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
