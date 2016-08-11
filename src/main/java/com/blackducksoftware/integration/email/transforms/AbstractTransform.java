package com.blackducksoftware.integration.email.transforms;

import java.util.List;

import com.blackducksoftware.integration.email.model.EmailContentItem;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public abstract class AbstractTransform {
	public final String KEY_PROJECT_NAME = "projectName";
	public final String KEY_PROJECT_VERSION = "projectVersionName";
	public final String KEY_COMPONENT_NAME = "componentName";
	public final String KEY_COMPONENT_VERSION = "componentVersionName";

	private final NotificationService notificationService;

	public AbstractTransform(final NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public abstract List<EmailContentItem> transform(NotificationItem item);
}
