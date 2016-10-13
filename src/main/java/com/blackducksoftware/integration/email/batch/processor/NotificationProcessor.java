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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.batch.processor.converter.IItemConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyOverrideConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyViolationClearedConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.PolicyViolationConverter;
import com.blackducksoftware.integration.email.batch.processor.converter.VulnerabilityConverter;
import com.blackducksoftware.integration.email.model.batch.CategoryDataBuilder;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.model.batch.ProjectDataBuilder;
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
		final Collection<ProjectData> projectMap = createCateoryDataMap(eventMap);
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
			if (event.getCategoryType() == NotificationCategoryEnum.VULNERABILITY) {
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

	private Collection<ProjectData> createCateoryDataMap(final Map<String, NotificationEvent> eventMap) {
		final Map<String, ProjectDataBuilder> projectDataMap = new LinkedHashMap<>();
		for (final Map.Entry<String, NotificationEvent> entry : eventMap.entrySet()) {
			final NotificationEvent event = entry.getValue();
			final String projectKey = event.getProjectKey();
			// get category map from the project or create the project data if
			// it doesn't exist
			Map<NotificationCategoryEnum, CategoryDataBuilder> categoryBuilderMap;
			if (!projectDataMap.containsKey(projectKey)) {
				final ProjectDataBuilder projectBuilder = new ProjectDataBuilder();
				projectBuilder.setProjectName(event.getProjectName());
				projectBuilder.setProjectVersion(event.getProjectVersion());
				projectDataMap.put(projectKey, projectBuilder);
				categoryBuilderMap = projectBuilder.getCategoryBuilderMap();
			} else {
				final ProjectDataBuilder projectBuilder = projectDataMap.get(projectKey);
				categoryBuilderMap = projectBuilder.getCategoryBuilderMap();
			}
			// get the category data object to be able to add items.
			CategoryDataBuilder categoryData;
			final NotificationCategoryEnum categoryKey = event.getCategoryType();
			if (!categoryBuilderMap.containsKey(categoryKey)) {
				categoryData = new CategoryDataBuilder();
				categoryData.setCategoryKey(categoryKey.name());
				categoryBuilderMap.put(categoryKey, categoryData);
			} else {
				categoryData = categoryBuilderMap.get(categoryKey);
			}
			categoryData.incrementItemCount(event.getCategoryItemCount());
			categoryData.addItem(new ItemData(event.getDataSet()));
		}
		// build
		final Collection<ProjectData> dataList = new LinkedList<>();
		for (final ProjectDataBuilder builder : projectDataMap.values()) {
			dataList.add(builder.build());
		}
		return dataList;
	}

	private List<NotificationEvent> createVulnerabilityEvents(final NotificationEvent originalEvent) {
		final List<NotificationEvent> eventList = new LinkedList<>();
		final Map<NotificationCategoryEnum, NotificationEvent> eventMap = new HashMap<>();
		for (final String vulnerabilityId : originalEvent.getVulnerabilityIdSet()) {
			final VulnerabilityItem vulnerability = getVulnerabilities(vulnerabilityId);
			final NotificationCategoryEnum eventCategory = getEventCategory(vulnerability.getSeverity());
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
				event.getDataSet().add(new ItemEntry(ItemTypeEnum.COUNT.name(), String.valueOf(size)));
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

	private NotificationCategoryEnum getEventCategory(final String severityString) {
		final SeverityEnum severity = SeverityEnum.getSeverityEnum(severityString);

		switch (severity) {
		case HIGH: {
			return NotificationCategoryEnum.HIGH_VULNERABILITY;
		}
		case MEDIUM: {
			return NotificationCategoryEnum.MEDIUM_VULNERABILITY;
		}
		case LOW: {
			return NotificationCategoryEnum.LOW_VULNERABILITY;
		}
		default: {
			return NotificationCategoryEnum.VULNERABILITY;
		}
		}
	}
}
