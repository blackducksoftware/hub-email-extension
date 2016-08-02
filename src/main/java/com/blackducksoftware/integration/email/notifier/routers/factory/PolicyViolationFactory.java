package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;

@Component
public class PolicyViolationFactory extends AbstractEmailFactory<RuleViolationNotificationItem> {

	public PolicyViolationFactory(final EmailMessagingService emailMessagingService) {
		super(emailMessagingService);
	}

	@Override
	public ItemRouter<List<RuleViolationNotificationItem>> createInstance(
			final RouterTaskData<List<RuleViolationNotificationItem>> data) {
		final PolicyViolationRouter router = new PolicyViolationRouter(getEmailMessagingService());
		router.setTaskData(data);
		return router;
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(RuleViolationNotificationItem.class.getName());
		return topicSet;
	}
}
