package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;

@Component
public class PolicyViolationFactory extends AbstractEmailFactory {

	public PolicyViolationFactory(final EmailMessagingService emailMessagingService) {
		super(emailMessagingService);
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		final PolicyViolationRouter router = new PolicyViolationRouter(getEmailMessagingService(), data);
		return router;
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(RuleViolationNotificationItem.class.getName());
		return topicSet;
	}
}
