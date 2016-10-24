package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;

public class SubProcessorCache<T extends NotificationEvent<?>> {
    private final Map<String, T> eventMap = new LinkedHashMap<>(500);

    public boolean hasEvent(String eventKey) {
        return eventMap.containsKey(eventKey);
    }

    public void addEvent(final T event) {
        final String key = event.getEventKey();
        if (!eventMap.containsKey(key)) {
            eventMap.put(key, event);
        } else {
            final T storedEvent = eventMap.get(key);
            final Set<ItemEntry> storedEventDataMap = storedEvent.getDataSet();
            final Set<ItemEntry> eventDataMap = event.getDataSet();
            storedEventDataMap.addAll(eventDataMap);
        }
    }

    public void removeEvent(final String eventKey) {
        if (eventMap.containsKey(eventKey)) {
            eventMap.remove(eventKey);
        }
    }

    public void removeEvent(final T event) {
        final String key = event.getEventKey();
        removeEvent(key);
    }

    public T getEvent(final String eventKey) {
        return eventMap.get(eventKey);
    }

    public Collection<T> getEvents() {
        return eventMap.values();
    }

    public Map<String, T> getEventMap() {
        return eventMap;
    }
}
