package com.blackducksoftware.integration.email.batch.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.batch.processor.converter.IItemConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyOverrideConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyViolationClearedConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyViolationConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.VulnerabilityConverter;
import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.api.vulnerabilities.SeverityEnum;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;

public class NotificationProcessor {
	private final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);
	private final Map<Class<?>, IItemConverter> converterMap = new HashMap<>();
	private final DataServicesFactory dataServicesFactory;

	// TODO this class needs cleanup. Too complicated and inefficient.
	public NotificationProcessor(final DataServicesFactory dataServicesFactory) {
		this.dataServicesFactory = dataServicesFactory;
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
		final Map<String, NotificationEvent> eventMap = processNotificationEvents(events);
		final Map<NotificationCategory, CategoryData> categoryMap = createCateoryDataMap(eventMap);
		final Collection<ProjectData> projectMap = createProjectData(categoryMap);
		return projectMap;
	}

	private Map<String, NotificationEvent> processNotificationEvents(final List<NotificationEvent> events) {
		final Map<String, NotificationEvent> eventMap = new LinkedHashMap<>(events.size());
		final Map<String, NotificationEvent> tempMap = new LinkedHashMap<>(events.size());
		for (final NotificationEvent event : events) {
			final String key = event.getEventKey();
			switch (event.getAction()) {
			case REMOVE: {
				if (tempMap.containsKey(key)) {
					final NotificationEvent storedEvent = tempMap.get(key);
					final Set<ItemEntry> eventDataMap = event.getDataSet();

					if (!storedEvent.getVulnerabilityIdSet().isEmpty() && !event.getVulnerabilityIdSet().isEmpty()) {
						storedEvent.getVulnerabilityIdSet().removeAll(event.getVulnerabilityIdSet());
					}
					if (storedEvent.getVulnerabilityIdSet().isEmpty()) {
						storedEvent.getDataSet().removeAll(eventDataMap);
						if (storedEvent.getDataSet().isEmpty()) {
							tempMap.remove(key);
						}
					}
				}
				break;
			}
			default:
			case ADD: {
				if (!tempMap.containsKey(key)) {
					tempMap.put(key, event);
				} else {
					final NotificationEvent storedEvent = tempMap.get(key);
					final Set<ItemEntry> storedEventDataMap = storedEvent.getDataSet();
					final Set<ItemEntry> eventDataMap = event.getDataSet();
					storedEventDataMap.addAll(eventDataMap);
					if (!event.getVulnerabilityIdSet().isEmpty()) {
						storedEvent.getVulnerabilityIdSet().addAll(event.getVulnerabilityIdSet());
					}
				}
				break;
			}
			}
		}

		// create additional vulnerability events
		for (final Map.Entry<String, NotificationEvent> entry : tempMap.entrySet()) {
			final NotificationEvent event = entry.getValue();
			if (event.getCategoryType() == NotificationCategory.CATEGORY_VULNERABILITY) {
				final List<NotificationEvent> vulnerabilityEvents = createVulnerabilityEvents(event);
				for (final NotificationEvent vulnerability : vulnerabilityEvents) {
					eventMap.put(vulnerability.getEventKey(), vulnerability);
				}
			} else {
				eventMap.put(event.getEventKey(), event);
			}
		}
		return eventMap;
	}

	private Map<NotificationCategory, CategoryData> createCateoryDataMap(
			final Map<String, NotificationEvent> eventMap) {
		final Map<NotificationCategory, CategoryData> categoryMap = new TreeMap<>();
		for (final Map.Entry<String, NotificationEvent> entry : eventMap.entrySet()) {
			final NotificationEvent event = entry.getValue();
			CategoryData categoryData;
			final NotificationCategory categoryKey = event.getCategoryType();
			if (!categoryMap.containsKey(categoryKey)) {
				categoryData = new CategoryData(event.getProjectName(), event.getProjectVersion(), categoryKey.name(),
						new LinkedList<>());
				categoryMap.put(categoryKey, categoryData);
			} else {
				categoryData = categoryMap.get(categoryKey);
			}
			categoryData.getItemList().add(new ItemData(event.getDataSet()));
		}

		return categoryMap;
	}

	private Collection<ProjectData> createProjectData(final Map<NotificationCategory, CategoryData> categoryMap) {
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

	private List<NotificationEvent> createVulnerabilityEvents(final NotificationEvent originalEvent) {
		final List<NotificationEvent> eventList = new LinkedList<>();
		final Map<NotificationCategory, NotificationEvent> eventMap = new HashMap<>();
		for (final String vulnerabilityId : originalEvent.getVulnerabilityIdSet()) {
			final VulnerabilityItem vulnerability = getVulnerabilities(vulnerabilityId);
			final NotificationCategory eventCategory = getEventCategory(vulnerability.getSeverity());
			if (eventMap.containsKey(eventCategory)) {
				final NotificationEvent event = eventMap.get(eventCategory);
				event.getVulnerabilityIdSet().add(vulnerabilityId);
			} else {
				final Set<String> vulnset = new HashSet<>();
				vulnset.add(vulnerabilityId);
				final LinkedHashSet<ItemEntry> newDataSet = new LinkedHashSet<>();
				newDataSet.addAll(originalEvent.getDataSet());
				final NotificationEvent event = new NotificationEvent(originalEvent.getAction(),
						originalEvent.getProjectName(), originalEvent.getProjectVersion(),
						originalEvent.getComponentName(), originalEvent.getComponentVersion(), eventCategory,
						newDataSet, vulnset);
				eventMap.put(eventCategory, event);
				eventList.add(event);
			}
		}
		for (final NotificationEvent event : eventList) {
			final int size = event.getVulnerabilityIdSet().size();
			if (size > 1) {
				event.getDataSet()
						.add(new ItemEntry(NotificationItemType.ITEM_TYPE_COUNT.name(), String.valueOf(size)));
			}
		}
		return eventList;
	}

	private VulnerabilityItem getVulnerabilities(final String vulnerabilityId) {
		try {
			return dataServicesFactory.getVulnerabilityRestService().getVulnerability(vulnerabilityId);
		} catch (IOException | URISyntaxException | BDRestException e) {
			logger.error("error getting vulnerability data", e);
			return null;
		}
	}

	private NotificationCategory getEventCategory(final String severityString) {
		final SeverityEnum severity = SeverityEnum.getSeverityEnum(severityString);

		switch (severity) {
		case HIGH: {
			return NotificationCategory.CATEGORY_HIGH_VULNERABILITY;
		}
		case MEDIUM: {
			return NotificationCategory.CATEGORY_MEDIUM_VULNERABILITY;
		}
		case LOW: {
			return NotificationCategory.CATEGORY_LOW_VULNERABILITY;
		}
		default: {
			return NotificationCategory.CATEGORY_VULNERABILITY;
		}
		}
	}
}
