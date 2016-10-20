package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collection;

import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;

public abstract class NotificationSubProcessor {

	private final SubProcessorCache cache;

	public NotificationSubProcessor(final SubProcessorCache cache) {
		this.cache = cache;
	}

	public Collection<NotificationEvent> getEvents() {
		return cache.getEvents();
	}

	public SubProcessorCache getCache() {
		return cache;
	}

	public String generateEventKey(final String projectName, final String projectVersion, final String componentName,
			final String componentVersion, final String category) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		result = prime * result + ((projectVersion == null) ? 0 : projectVersion.hashCode());
		result = prime * result + ((componentName == null) ? 0 : componentName.hashCode());
		result = prime * result + ((componentVersion == null) ? 0 : componentVersion.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		final String eventKey = String.valueOf(result);
		return eventKey;
	}

	public abstract void process(NotificationContentItem notification);
}
