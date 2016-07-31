package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.ItemRouterFactory;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationRouter;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;

@Component
public class PolicyViolationFactory extends ItemRouterFactory<List<RuleViolationNotificationItem>> {

	@Override
	public ItemRouter<List<RuleViolationNotificationItem>> createInstance(
			final RouterTaskData<List<RuleViolationNotificationItem>> data) {
		return new PolicyViolationRouter(data);
	}

}
