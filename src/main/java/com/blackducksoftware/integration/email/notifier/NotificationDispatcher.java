package com.blackducksoftware.integration.email.notifier;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.EncryptionException;
import com.blackducksoftware.integration.hub.exception.ResourceDoesNotExistException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.item.HubItemsService;
import com.blackducksoftware.integration.hub.meta.MetaInformation;
import com.blackducksoftware.integration.hub.meta.MetaLink;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.notification.api.VulnerabilityNotificationItem;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.reflect.TypeToken;

public class NotificationDispatcher extends AbstractPollingDispatcher {
	public static long DEFAULT_POLLING_INTERVAL_SECONDS = 10;
	public static long DEFAULT_POLLING_DELAY_SECONDS = 5;

	private final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

	private final HubServerConfig hubServerConfig;
	private final DateFormat notificationDateFormatter;
	private final Date applicationStartDate;
	private final CustomerProperties systemProperties;
	private ExecutorService executorService;

	public NotificationDispatcher(final HubServerConfig hubConfig, final DateFormat notificationDateFormatter,
			final Date applicationStartDate, final CustomerProperties systemProperties,
			final ExecutorService executorService) {
		this.hubServerConfig = hubConfig;
		this.notificationDateFormatter = notificationDateFormatter;
		this.applicationStartDate = applicationStartDate;
		this.systemProperties = systemProperties;
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
	public Map<String, EmailTaskData> fetchRouterConfig() {
		Map<String, EmailTaskData> dataMap = new HashMap<>();
		if (hubServerConfig != null) {
			try {
				final RestConnection restConnection = initRestConnection();
				final HubItemsService<NotificationItem> hubItemsService = initHubItemsService(restConnection);
				final Date startDate = findStartDate();
				dataMap = fetchNotifications(hubItemsService, startDate, getCurrentRun());

			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			dataMap = createNotificationTestData();
		}

		return dataMap;
	}

	private RestConnection initRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = new RestConnection(hubServerConfig.getHubUrl().toString());

		restConnection.setCookies(hubServerConfig.getGlobalCredentials().getUsername(),
				hubServerConfig.getGlobalCredentials().getDecryptedPassword());
		restConnection.setProxyProperties(hubServerConfig.getProxyInfo());

		restConnection.setTimeout(hubServerConfig.getTimeout());
		return restConnection;
	}

	private Date findStartDate() {
		if (getLastRun() == null) {
			return applicationStartDate;
		} else {
			return getLastRun();
		}
	}

	private HubItemsService<NotificationItem> initHubItemsService(final RestConnection restConnection) {
		final TypeToken<NotificationItem> typeToken = new TypeToken<NotificationItem>() {
		};
		final Map<String, Class<? extends NotificationItem>> typeToSubclassMap = new HashMap<>();
		typeToSubclassMap.put("VULNERABILITY", VulnerabilityNotificationItem.class);
		typeToSubclassMap.put("RULE_VIOLATION", RuleViolationNotificationItem.class);
		typeToSubclassMap.put("POLICY_OVERRIDE", PolicyOverrideNotificationItem.class);
		final HubItemsService<NotificationItem> hubItemsService = new HubItemsService<>(restConnection,
				NotificationItem.class, typeToken, typeToSubclassMap);
		return hubItemsService;
	}

	private Map<String, EmailTaskData> fetchNotifications(final HubItemsService<NotificationItem> hubItemsService,
			final Date startDate, final Date endDate) throws Exception {
		// TODO may need chunking and maybe retry logic to
		final int limit = 1000;

		// handle large sets
		final String startDateString = notificationDateFormatter.format(startDate);
		final String endDateString = notificationDateFormatter.format(endDate);
		final List<String> urlSegments = new ArrayList<>();
		urlSegments.add("api");
		urlSegments.add("notifications");

		final Set<AbstractMap.SimpleEntry<String, String>> queryParameters = new HashSet<>();
		queryParameters.add(new AbstractMap.SimpleEntry<>("startDate", startDateString));
		queryParameters.add(new AbstractMap.SimpleEntry<>("endDate", endDateString));
		queryParameters.add(new AbstractMap.SimpleEntry<>("limit", String.valueOf(limit)));
		final Map<String, EmailTaskData> items = new HashMap<>();
		try {
			final List<NotificationItem> notificationItems = hubItemsService.httpGetItemList(urlSegments,
					queryParameters);
			final Map<String, List<Object>> partitionMap = createPartitionMap(notificationItems);
			final Set<String> topicSet = partitionMap.keySet();
			for (final String topic : topicSet) {
				items.put(topic, new EmailTaskData(partitionMap.get(topic)));
			}

		} catch (IOException | URISyntaxException | ResourceDoesNotExistException | BDRestException e) {
			throw new RuntimeException("Error parsing NotificationItemList: " + e.getMessage(), e);
		}
		return items;
	}

	private Map<String, List<Object>> createPartitionMap(final List<NotificationItem> notificationItems) {
		final Map<String, List<Object>> partitionMap = new HashMap<>();
		partitionMap.put(AbstractEmailFactory.TOPIC_ALL, new Vector<Object>());
		for (final NotificationItem notification : notificationItems) {
			partitionMap.get(AbstractEmailFactory.TOPIC_ALL).add(notification);
			final String classname = notification.getClass().getName();
			List<Object> partitionList;
			if (partitionMap.containsKey(classname)) {
				partitionList = partitionMap.get(classname);
			} else {
				partitionList = new Vector<Object>();
				partitionMap.put(classname, partitionList);
			}
			partitionList.add(notification);
		}

		return partitionMap;
	}

	private Map<String, EmailTaskData> createNotificationTestData() {
		final Map<String, EmailTaskData> dataMap = new HashMap<>();
		final List<Object> list = new Vector<>();
		for (int selectedClass = 0; selectedClass < 3; selectedClass++) {
			final Class<? extends NotificationItem> clazz;
			final List<String> allow = new ArrayList<>();
			final List<MetaLink> links = new ArrayList<>();
			final MetaInformation meta = new MetaInformation(allow, "", links);
			final int amount = new Double(Math.random() * 100).intValue() * 1000;

			final boolean generateEvents = new Double(Math.random() * 100).intValue() % 2 == 1;
			if (generateEvents) {
				switch (selectedClass) {
				case 0: {
					logger.info("GENERATING TEST DATA: Creating VulnerabilityNotificationItem: " + amount);
					clazz = VulnerabilityNotificationItem.class;
					break;
				}
				case 1: {
					logger.info("GENERATING TEST DATA: Creating RuleViolationNotificationItem: " + amount);
					clazz = RuleViolationNotificationItem.class;
					break;
				}
				case 2: {
					logger.info("GENERATING TEST DATA: Creating PolicyOverrideNotificationItem: " + amount);
					clazz = PolicyOverrideNotificationItem.class;
					break;
				}
				default: {
					logger.info("GENERATING TEST DATA: Default Creating RuleViolationNotificationItem: " + amount);
					clazz = RuleViolationNotificationItem.class;
					break;
				}
				}
				for (int index = 0; index < amount; index++) {
					try {
						final Constructor<?> constructor = clazz.getDeclaredConstructor(MetaInformation.class);
						if (constructor != null) {
							list.add(constructor.newInstance(meta));
						}
					} catch (final InstantiationException | IllegalAccessException | NoSuchMethodException
							| SecurityException | IllegalArgumentException | InvocationTargetException e) {
						logger.error("GENERATING TEST DATA: Error", e);
					}
				}
				dataMap.put(clazz.getName(), new EmailTaskData(list));
			}
		}
		return dataMap;
	}
}
