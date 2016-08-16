package com.blackducksoftware.integration.email.notifier;

import java.io.IOException;
import java.net.URISyntaxException;
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
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.api.user.UserItem;
import com.blackducksoftware.integration.hub.dataservices.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;

public class NotificationDispatcher extends AbstractPollingDispatcher {
	public static long DEFAULT_POLLING_INTERVAL_SECONDS = 10;
	public static long DEFAULT_POLLING_DELAY_SECONDS = 5;

	private final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

	private final HubServerConfig hubServerConfig;
	private final Date applicationStartDate;
	private final CustomerProperties systemProperties;
	private final ExecutorService executorService;
	private final NotificationDataService notificationService;
	private final UserRestService userRestService;

	public NotificationDispatcher(final HubServerConfig hubConfig, final Date applicationStartDate,
			final CustomerProperties systemProperties, final ExecutorService executorService,
			final NotificationDataService notificationService, final UserRestService userRestService) {
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
	public List<NotificationContentItem> fetchData() {
		List<NotificationContentItem> contentList = new Vector<>();
		if (hubServerConfig != null) {
			final Date startDate = findStartDate();
			contentList = fetchNotifications(startDate, getCurrentRun());
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

	private List<NotificationContentItem> fetchNotifications(final Date startDate, final Date endDate) {
		List<NotificationContentItem> items = new Vector<>();
		try {
			items = notificationService.getAllNotifications(startDate, endDate);
		} catch (IOException | URISyntaxException | BDRestException e) {
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

	private List<UserItem> fetchUsers() {
		List<UserItem> userItems = new ArrayList<>();
		try {
			userItems = userRestService.getAllUsers();
		} catch (IOException | URISyntaxException | BDRestException e) {
			logger.error("Error occurred fetching users.", e);
		}
		return userItems;
	}

}
