package com.blackducksoftware.integration.email.transforms.templates;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;

public abstract class AbstractContentTransform {
	public final String KEY_PROJECT_NAME = "projectName";
	public final String KEY_PROJECT_VERSION = "projectVersionName";
	public final String KEY_COMPONENT_NAME = "componentName";
	public final String KEY_COMPONENT_VERSION = "componentVersionName";

	public abstract List<Map<String, Object>> transform(NotificationContentItem item);

	public abstract String getContentItemType();
}
