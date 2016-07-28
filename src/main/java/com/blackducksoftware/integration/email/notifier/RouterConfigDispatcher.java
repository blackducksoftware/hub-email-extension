package com.blackducksoftware.integration.email.notifier;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.AbstractPollingDispatcher;
import com.blackducksoftware.integration.email.messaging.events.ConfigureEventDispatcher;
import com.blackducksoftware.integration.email.messaging.events.ConfigureMessageListener;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;

@Component
public class RouterConfigDispatcher
		extends AbstractPollingDispatcher<ConfigureMessageListener<EmailSystemProperties>, EmailSystemProperties> {

	private final Logger logger = LoggerFactory.getLogger(RouterConfigDispatcher.class);

	public static long DEFAULT_POLLING_INTERVAL_SECONDS = 60;
	public static long DEFAULT_POLLING_DELAY_SECONDS = 2;

	@Autowired
	private EmailSystemProperties systemProperties;

	@PostConstruct
	@Override
	public void initDispatcher() {
		setName("Router Configuration Dispatcher");
		setEventDispatcher(new ConfigureEventDispatcher<EmailSystemProperties>());

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
	public Map<String, EmailSystemProperties> createEventData() {
		final Map<String, EmailSystemProperties> eventDataMap = new HashMap<>();
		logger.debug("Fetching Email system configuration data");
		eventDataMap.put("emailconfigtopic", new EmailSystemProperties());
		return eventDataMap;
	}

}
