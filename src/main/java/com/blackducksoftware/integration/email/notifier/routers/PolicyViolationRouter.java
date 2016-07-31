package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;

public class PolicyViolationRouter extends AbstractEmailRouter<RuleViolationNotificationItem> {

	public PolicyViolationRouter(final RouterTaskData<List<RuleViolationNotificationItem>> data) {
		super(data);
	}

	@Override
	public EmailData transform(final List<RuleViolationNotificationItem> data) {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> emailDataMap = new HashMap<>();
		return new EmailData(addresses, emailDataMap);
	}
}
