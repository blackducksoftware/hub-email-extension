package com.blackducksoftware.integration.email.notifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.factory.AbstractEmailFactory;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.api.user.UserItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;
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
	private final UserRestService userRestService;

	public NotificationDispatcher(final HubServerConfig hubConfig, final Date applicationStartDate,
			final CustomerProperties systemProperties, final ExecutorService executorService,
			final NotificationService notificationService, final UserRestService userRestService) {
		this.hubServerConfig = hubConfig;
		this.applicationStartDate = applicationStartDate;
		this.systemProperties = systemProperties;
		this.executorService = executorService;
		this.notificationService = notificationService;
		this.userRestService = userRestService;
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
	public Map<String, EmailTaskData> fetchData() {
		final List<NotificationItem> itemList;
		if (hubServerConfig != null) {
			final Date startDate = findStartDate();
			itemList = fetchNotifications(startDate, getCurrentRun());
		} else {
			itemList = new Vector<>();
		}
		return partitionData(itemList);
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

	private List<UserItem> fetchUsers() {
		List<UserItem> userItems = new ArrayList<>();
		try {
			userItems = userRestService.getAllUsers();
		} catch (IOException | URISyntaxException | BDRestException e) {
			logger.error("Error occurred fetching users.", e);
		}
		return userItems;
	}

	private Map<String, EmailTaskData> partitionData(final List<NotificationItem> itemList) {
		final Map<String, EmailTaskData> dataMap = new HashMap<>();
		final Map<String, List<Object>> partitionMap = createPartitionMap(itemList);
		final Set<String> topicSet = partitionMap.keySet();
		for (final String topic : topicSet) {
			dataMap.put(topic, new EmailTaskData(partitionMap.get(topic)));
		}

		return dataMap;
	}

	private Map<String, List<Object>> createPartitionMap(final List<NotificationItem> notificationItems) {
		final Map<String, List<Object>> partitionMap = new HashMap<>();
		partitionMap.put(AbstractEmailFactory.TOPIC_ALL, new Vector<>());
		for (final NotificationItem notification : notificationItems) {
			partitionMap.get(AbstractEmailFactory.TOPIC_ALL).add(notification);
			final String classname = notification.getClass().getName();
			List<Object> partitionList;
			if (partitionMap.containsKey(classname)) {
				partitionList = partitionMap.get(classname);
			} else {
				partitionList = new Vector<>();
				partitionMap.put(classname, partitionList);
			}
			partitionList.add(notification);
		}

		return partitionMap;
	}
}
