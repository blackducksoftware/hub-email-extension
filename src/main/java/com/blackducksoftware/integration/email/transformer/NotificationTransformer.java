package com.blackducksoftware.integration.email.transformer;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.hub.dataservices.notifications.items.NotificationContentItem;

public abstract class NotificationTransformer {
	public static final String KEY_PROJECT_NAME = "projectName";
	public static final String KEY_PROJECT_VERSION = "projectVersionName";
	public static final String KEY_COMPONENT_NAME = "componentName";
	public static final String KEY_COMPONENT_VERSION = "componentVersionName";

	public static final String KEY_POLICY_NAME = "policyName";

	public static final String KEY_FIRST_NAME = "firstName";
	public static final String KEY_LAST_NAME = "lastName";

	public static final String KEY_VULN_ADDED = "vulnAddedList";
	public static final String KEY_VULN_UPDATED = "vulnUpdatedList";
	public static final String KEY_VULN_DELETED = "vulnDeletedList";

	public Map<String, String> transformNotificationContentItem(final NotificationContentItem notificationContentItem) {
		final Map<String, String> map = new HashMap<>();
		map.put(KEY_PROJECT_NAME, notificationContentItem.getProjectVersion().getProjectName());
		map.put(KEY_PROJECT_VERSION, notificationContentItem.getProjectVersion().getProjectVersionName());
		map.put(KEY_COMPONENT_NAME, notificationContentItem.getComponentName());
		map.put(KEY_COMPONENT_VERSION, notificationContentItem.getComponentVersion());

		return map;
	}

	public abstract FreemarkerTarget transform(NotificationContentItem notification);

}
