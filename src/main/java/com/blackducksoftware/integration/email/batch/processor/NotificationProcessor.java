package com.blackducksoftware.integration.email.batch.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.batch.processor.converter.IItemConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyOverrideConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyViolationClearedConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyViolationConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.VulnerabilityConverter;
import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

public class NotificationProcessor {
	private final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);
	private final Map<Class<?>, IItemConverter> converterMap = new HashMap<>();

	public NotificationProcessor(final DataServicesFactory dataServicesFactory) {
		converterMap.put(PolicyViolationContentItem.class, new PolicyViolationConverter());
		converterMap.put(PolicyViolationClearedContentItem.class, new PolicyViolationClearedConverter());
		converterMap.put(PolicyOverrideContentItem.class, new PolicyOverrideConverter());
		converterMap.put(VulnerabilityContentItem.class, new VulnerabilityConverter(dataServicesFactory));
	}

	public Collection<ProjectData> process(final SortedSet<NotificationContentItem> notifications) {
		final List<NotificationEvent> events = createEvents(notifications);
		final Collection<ProjectData> projectDataList = processEvents(events);
		return projectDataList;
	}

	private List<NotificationEvent> createEvents(final SortedSet<NotificationContentItem> notifications) {
		final List<NotificationEvent> eventList = new LinkedList<>();
		for (final NotificationContentItem item : notifications) {
			final Class<?> key = item.getClass();
			if (!converterMap.containsKey(key)) {
				logger.error("Could not find converter for notification: {}", item);
			} else {
				final IItemConverter converter = converterMap.get(key);
				eventList.addAll(converter.convert(item));
			}
		}
		return eventList;
	}

	private Collection<ProjectData> processEvents(final List<NotificationEvent> events) {
		// TODO Streams work better?
		final Map<String, NotificationEvent> eventMap = new LinkedHashMap<>(events.size());
		for (final NotificationEvent event : events) {
			final String key = event.getEventKey();
			switch (event.getAction()) {
			case REMOVE: {
				if (eventMap.containsKey(key)) {
					final NotificationEvent storedEvent = eventMap.get(key);
					final Map<String, String> storedEventDataMap = storedEvent.getDataMap();
					final Map<String, String> eventDataMap = event.getDataMap();
					for (final String mapKey : eventDataMap.keySet()) {
						storedEvent.getDataMap().remove(mapKey);
					}
					if (storedEventDataMap.isEmpty()) {
						eventMap.remove(key);
					}
				}
				break;
			}
			default:
			case ADD: {
				if (!eventMap.containsKey(key)) {
					eventMap.put(key, event);
				} else {
					final NotificationEvent storedEvent = eventMap.get(key);
					final Map<String, String> storedEventDataMap = storedEvent.getDataMap();
					final Map<String, String> eventDataMap = event.getDataMap();
					for (final Map.Entry<String, String> entry : eventDataMap.entrySet()) {
						storedEventDataMap.put(entry.getKey(), entry.getValue());
					}
				}
				break;
			}
			}
		}
		final Map<String, CategoryData> categoryMap = new LinkedHashMap<>();
		for (final Map.Entry<String, NotificationEvent> entry : eventMap.entrySet()) {
			final NotificationEvent event = entry.getValue();
			CategoryData categoryData;
			final String categoryKey = event.getCategoryType();
			if (!categoryMap.containsKey(categoryKey)) {
				categoryData = new CategoryData(event.getProjectName(), event.getProjectVersion(), categoryKey,
						new LinkedList<>());
				categoryMap.put(categoryKey, categoryData);
			} else {
				categoryData = categoryMap.get(categoryKey);
			}
			categoryData.getItemList().add(new ItemData(event.getDataMap()));
		}

		final Map<String, ProjectData> projectMap = new LinkedHashMap<>();
		for (final CategoryData categoryData : categoryMap.values()) {
			final String projectKey = categoryData.getProjectKey();
			List<CategoryData> categoryList;
			if (!projectMap.containsKey(projectKey)) {
				categoryList = new LinkedList<>();
				final ProjectData projectData = new ProjectData(categoryData.getProjectName(),
						categoryData.getProjectVersion(), categoryList);
				projectMap.put(projectData.getProjectKey(), projectData);
			} else {
				final ProjectData projectData = projectMap.get(projectKey);
				categoryList = projectData.getCategoryList();
			}
			categoryList.add(categoryData);
		}

		return projectMap.values();
	}
}
