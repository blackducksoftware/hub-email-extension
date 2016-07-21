package com.blackducksoftware.integration.email.notifier.rules;

import com.blackducksoftware.integration.email.dto.ConfigResponse;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.messaging.AbstractMessageRouter;

public abstract class AbstractNotificationFilter<T extends NotificationItem>
		extends AbstractMessageRouter<ConfigResponse, T> {

	@SuppressWarnings("unchecked")
	public void apply(final Object obj) {
		route((T) obj);
	}
}
