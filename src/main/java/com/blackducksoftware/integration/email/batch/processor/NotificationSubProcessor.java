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

	public abstract void process(NotificationContentItem notification);
}
