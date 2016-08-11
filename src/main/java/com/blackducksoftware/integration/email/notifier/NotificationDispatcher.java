package com.blackducksoftware.integration.email.notifier;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.EmailContentItem;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.transforms.AbstractTransform;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.exception.NotificationServiceException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.notification.NotificationDateRange;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class NotificationDispatcher extends AbstractPollingDispatcher {
	public static long DEFAULT_POLLING_INTERVAL_SECONDS = 10;
	public static long DEFAULT_POLLING_DELAY_SECONDS = 5;

	private final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

	private final HubServerConfig hubServerConfig;
	private final Date applicationStartDate;
	private final CustomerProperties systemProperties;
	private final ExecutorService executorService;
	private final NotificationService notificationService;
	private final Map<String, AbstractTransform> notificationTransformMap;

	public NotificationDispatcher(final HubServerConfig hubConfig, final Date applicationStartDate,
			final CustomerProperties systemProperties, final ExecutorService executorService,
			final NotificationService notificationService, final Map<String, AbstractTransform> transformMap) {
		this.hubServerConfig = hubConfig;
		this.applicationStartDate = applicationStartDate;
		this.systemProperties = systemProperties;
		this.executorService = executorService;
		this.notificationService = notificationService;
		this.notificationTransformMap = transformMap;
	}

	@Override
	public void init() {
		setName("Notification Dispatcher");
		setExecutorService(executorService);
		// setup the delay and polling interval based on the property values
		// values in config file are assumed to be in seconds.
		final String intervalSeconds = systemProperties.getNotificationInterval();
		final String delaySeconds = systemProperties.getNotificationStartupDelay();
		final long interval = NumberUtils.toLong(intervalSeconds, DEFAULT_POLLING_INTERVAL_SECONDS);
		setInterval(interval * 1000);
		final long delay = NumberUtils.toLong(delaySeconds, DEFAULT_POLLING_DELAY_SECONDS);
		setStartupDelay(delay * 1000);
	}

	@Override
	public List<EmailContentItem> fetchData() {
		final List<EmailContentItem> contentList = new Vector<>();
		if (hubServerConfig != null) {
			final Date startDate = findStartDate();
			final List<NotificationItem> itemList = fetchNotifications(startDate, getCurrentRun());

			for (final NotificationItem item : itemList) {
				final Class<?> key = item.getClass();
				if (notificationTransformMap.containsKey(key.getName())) {
					contentList.addAll(notificationTransformMap.get(key.getName()).transform(item));
				}
			}
		}
		return contentList;
	}

	private Date findStartDate() {
		if (getLastRun() == null) {
			return applicationStartDate;
		} else {
			return getLastRun();
		}
	}

	private List<NotificationItem> fetchNotifications(final Date startDate, final Date endDate) {
		List<NotificationItem> items = new Vector<>();
		try {
			final NotificationDateRange dateRange = new NotificationDateRange(startDate, endDate);
			items = notificationService.fetchNotifications(dateRange);
		} catch (ParseException | NotificationServiceException e) {
			logger.error("Error occurred fetching notifications.", e);
		}
		return items;
	}

	@Override
	public Map<String, EmailTaskData> filterData(final Map<String, List<Object>> partitionedData) {
		final Map<String, EmailTaskData> templateDataMap = new HashMap<>();
		final Set<String> topicSet = partitionedData.keySet();
		for (final String topic : topicSet) {
			templateDataMap.put(topic, new EmailTaskData(partitionedData.get(topic)));
		}
		return templateDataMap;
	}
}
