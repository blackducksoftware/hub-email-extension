package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.List;

import com.blackducksoftware.integration.email.messaging.ItemRouterFactory;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

public abstract class AbstractEmailFactory<T extends NotificationItem> extends ItemRouterFactory<List<T>> {

}
