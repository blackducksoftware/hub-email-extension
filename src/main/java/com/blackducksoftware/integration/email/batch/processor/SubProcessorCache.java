package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;

public class SubProcessorCache {
	final Map<String, NotificationEvent> eventMap = new LinkedHashMap<>(500);

	public void addEvent(final NotificationEvent event) {
		final String key = event.getEventKey();
		if (!eventMap.containsKey(key)) {
			eventMap.put(key, event);
		} else {
			final NotificationEvent storedEvent = eventMap.get(key);
			final Set<ItemEntry> storedEventDataMap = storedEvent.getDataSet();
			final Set<ItemEntry> eventDataMap = event.getDataSet();
			storedEventDataMap.addAll(eventDataMap);
			if (!event.getVulnerabilityIdSet().isEmpty()) {
				storedEvent.getVulnerabilityIdSet().addAll(event.getVulnerabilityIdSet());
			}
		}
	}

	public void removeEvent(final NotificationEvent event) {
		final String key = event.getEventKey();
		if (eventMap.containsKey(key)) {
			final NotificationEvent storedEvent = eventMap.get(key);
			final Set<ItemEntry> eventDataMap = event.getDataSet();

			if (!storedEvent.getVulnerabilityIdSet().isEmpty() && !event.getVulnerabilityIdSet().isEmpty()) {
				storedEvent.getVulnerabilityIdSet().removeAll(event.getVulnerabilityIdSet());
			}
			if (storedEvent.getVulnerabilityIdSet().isEmpty()) {
				storedEvent.getDataSet().removeAll(eventDataMap);
				if (storedEvent.getDataSet().isEmpty()) {
					eventMap.remove(key);
				}
			}
		}
	}

	public NotificationEvent getEvent(final String eventKey) {
		return eventMap.get(eventKey);
	}

	public Collection<NotificationEvent> getEvents() {
		return eventMap.values();
	}
}
