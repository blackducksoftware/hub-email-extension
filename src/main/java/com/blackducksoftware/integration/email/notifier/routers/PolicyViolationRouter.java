package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.api.notification.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class PolicyViolationRouter extends AbstractEmailRouter<RuleViolationNotificationItem> {
	public PolicyViolationRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractContentTransform> transformMap, final EmailTaskData taskData) {
		super(emailMessagingService, customerProperties, notificationService, transformMap, taskData);
	}

	@Override
	public EmailData transform(final List<RuleViolationNotificationItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> emailDataMap = new HashMap<>();
		return new EmailData(addresses, emailDataMap);
	}

	@Override
	public String getTemplateName() {
		return TEMPLATE_DEFAULT;
	}

}
