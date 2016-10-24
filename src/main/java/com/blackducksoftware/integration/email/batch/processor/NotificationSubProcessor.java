package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collection;

import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;

public abstract class NotificationSubProcessor<T extends NotificationEvent<?>> {

    private final SubProcessorCache<T> cache;

    public NotificationSubProcessor(final SubProcessorCache<T> cache) {
        this.cache = cache;
    }

    public Collection<T> getEvents() {
        return cache.getEvents();
    }

    public SubProcessorCache<T> getCache() {
        return cache;
    }

    public abstract void process(NotificationContentItem notification);
}
