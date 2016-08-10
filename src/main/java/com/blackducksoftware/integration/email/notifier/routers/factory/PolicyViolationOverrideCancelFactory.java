package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyOverrideContentItem;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationOverrideCancelRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class PolicyViolationOverrideCancelFactory extends AbstractEmailFactory {
	public PolicyViolationOverrideCancelFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractContentTransform> transformMap) {
		super(emailMessagingService, customerProperties, notificationService, transformMap);
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		final PolicyViolationOverrideCancelRouter router = new PolicyViolationOverrideCancelRouter(
				getEmailMessagingService(), getCustomerProperties(), getNotificationService(), getTransformMap(), data);
		return router;
	}

	@Override
	public Set<String> getSubscriberTopics() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyOverrideContentItem.class.getName());
		return topicSet;
	}
}
