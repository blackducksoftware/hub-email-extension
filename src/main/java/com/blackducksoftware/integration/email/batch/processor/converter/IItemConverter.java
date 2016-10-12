package com.blackducksoftware.integration.email.batch.processor.converter;

import java.util.List;

import com.blackducksoftware.integration.email.batch.processor.NotificationEvent;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;

public interface IItemConverter {

	public List<NotificationEvent> convert(NotificationContentItem notification);
}
