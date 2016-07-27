package com.blackducksoftware.integration.email.notifier;

import java.io.IOException;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.AbstractPollingDispatcher;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.EncryptionException;
import com.blackducksoftware.integration.hub.exception.ResourceDoesNotExistException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.item.HubItemsService;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;
import com.blackducksoftware.integration.hub.notification.api.PolicyOverrideNotificationItem;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.notification.api.VulnerabilityNotificationItem;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.reflect.TypeToken;

@Component
public class NotificationDispatcher extends AbstractPollingDispatcher<NotificationItem> {

	@Autowired
	private HubServerConfig hubServerConfig;

	@Autowired
	private DateFormat notificationDateFormatter;

	@Autowired
	private Date applicationStartDate;

	@Override
	public List<NotificationItem> fetchMessages() {
		List<NotificationItem> messages = new ArrayList<NotificationItem>();

		if (hubServerConfig != null) {
			try {
				final RestConnection restConnection = initRestConnection();
				final HubItemsService<NotificationItem> hubItemsService = initHubItemsService(restConnection);
				final Date startDate = findStartDate();
				messages = fetchNotifications(hubItemsService, startDate, getCurrentRun());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return messages;
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

	private List<NotificationItem> fetchNotifications(final HubItemsService<NotificationItem> hubItemsService,
			final Date startDate, final Date endDate) throws Exception {

		final int limit = 1000; // TODO may need chunking and maybe retry logic
								// to
		// handle large sets

		final String startDateString = notificationDateFormatter.format(startDate);
		final String endDateString = notificationDateFormatter.format(endDate);
		final List<String> urlSegments = new ArrayList<>();
		urlSegments.add("api");
		urlSegments.add("notifications");

		final Set<AbstractMap.SimpleEntry<String, String>> queryParameters = new HashSet<>();
		queryParameters.add(new AbstractMap.SimpleEntry<String, String>("startDate", startDateString));
		queryParameters.add(new AbstractMap.SimpleEntry<String, String>("endDate", endDateString));
		queryParameters.add(new AbstractMap.SimpleEntry<String, String>("limit", String.valueOf(limit)));
		List<NotificationItem> items;
		try {
			items = hubItemsService.httpGetItemList(urlSegments, queryParameters);
		} catch (IOException | URISyntaxException | ResourceDoesNotExistException | BDRestException e) {
			throw new RuntimeException("Error parsing NotificationItemList: " + e.getMessage(), e);
		}
		return items;
	}
}