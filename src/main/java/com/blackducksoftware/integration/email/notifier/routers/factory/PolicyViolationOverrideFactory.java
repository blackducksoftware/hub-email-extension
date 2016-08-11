package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyOverrideContentItem;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationOverrideRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class PolicyViolationOverrideFactory extends AbstractEmailFactory {
	public PolicyViolationOverrideFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractContentTransform> transformMap) {
		super(emailMessagingService, customerProperties, notificationService, transformMap);
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		final PolicyViolationOverrideRouter router = new PolicyViolationOverrideRouter(getEmailMessagingService(),
				getCustomerProperties(), getNotificationService(), getTransformMap(), getTemplateName(), data);
		return router;
	}

	@Override
	public Set<String> getTemplateContentTypes() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyOverrideContentItem.class.getName());
		return topicSet;
	}

	@Override
	public String getTemplateName() {
		return "hubPolicyViolationOverride.ftl";
	}
}
