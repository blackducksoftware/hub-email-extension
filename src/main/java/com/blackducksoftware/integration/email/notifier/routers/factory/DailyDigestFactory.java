package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.PolicyOverrideContentItem;
import com.blackducksoftware.integration.email.model.PolicyViolationContentItem;
import com.blackducksoftware.integration.email.model.VulnerabilityContentItem;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.DailyDigestRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class DailyDigestFactory extends AbstractEmailFactory {

	public DailyDigestFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractContentTransform> transformMap) {
		super(emailMessagingService, customerProperties, notificationService, transformMap);
	}

	@Override
	public String getTemplateName() {
		return "dailyDigest.ftl";
	}

	@Override
	public Set<String> getTemplateContentTypes() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyViolationContentItem.class.getName());
		topicSet.add(PolicyOverrideContentItem.class.getName());
		topicSet.add(VulnerabilityContentItem.class.getName());
		return topicSet;
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		return new DailyDigestRouter(getEmailMessagingService(), getCustomerProperties(), getNotificationService(),
				getTransformMap(), getTemplateName(), data);
	}
}
