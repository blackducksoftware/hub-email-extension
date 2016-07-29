package com.blackducksoftware.integration.email.notifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.AbstractPollingDispatcher;
import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public class RouterConfigDispatcher extends
		AbstractPollingDispatcher<EmailSystemProperties, ItemRouter<EmailSystemProperties, List<? extends NotificationItem>, Map<String, Object>>> {

	private final Logger logger = LoggerFactory.getLogger(RouterConfigDispatcher.class);

	public static long DEFAULT_POLLING_INTERVAL_SECONDS = 60;
	public static long DEFAULT_POLLING_DELAY_SECONDS = 2;

	@Autowired
	private EmailSystemProperties systemProperties;

	@Autowired
	private ExecutorService executorService;

	@PostConstruct
	@Override
	public void init() {
		setName("Router Configuration Dispatcher");
		setExecutorService(executorService);
		// setup the delay and polling interval based on the property values
		// values in config file are assumed to be in seconds.
		final String intervalSeconds = systemProperties.getConfigurationInterval();
		final String delaySeconds = systemProperties.getConfigurationStartupDelay();
		final Long interval = NumberUtils.toLong(intervalSeconds, DEFAULT_POLLING_INTERVAL_SECONDS);
		setInterval(interval * 1000);
		final Long delay = NumberUtils.toLong(delaySeconds, DEFAULT_POLLING_DELAY_SECONDS);
		setStartupDelay(delay * 1000);
	}

	@Override
	public Map<String, EmailSystemProperties> fetchData() {
		final Map<String, EmailSystemProperties> eventDataMap = new HashMap<>();
		logger.debug("Fetching Email system configuration data");
		eventDataMap.put("emailconfigtopic", new EmailSystemProperties());
		return eventDataMap;
	}

	@Override
	public Runnable createEventTask(
			final ItemRouter<EmailSystemProperties, List<? extends NotificationItem>, Map<String, Object>> router,
			final EmailSystemProperties data) {
		return new ConfigureTask(router, data);
	}

	private class ConfigureTask implements Runnable {

		private final ItemRouter<EmailSystemProperties, List<? extends NotificationItem>, Map<String, Object>> router;
		private final EmailSystemProperties data;

		public ConfigureTask(
				final ItemRouter<EmailSystemProperties, List<? extends NotificationItem>, Map<String, Object>> router,
				final EmailSystemProperties data) {
			this.router = router;
			this.data = data;
		}

		@Override
		public void run() {
			router.configure(data);
		}
	}
}
