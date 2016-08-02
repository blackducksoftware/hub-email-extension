package com.blackducksoftware.integration.email.notifier.routers;

import java.util.List;

import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

public class EmailTaskData extends RouterTaskData<List<? extends NotificationItem>> {

	public EmailTaskData(final List<? extends NotificationItem> data) {
		super(data);
	}
}
