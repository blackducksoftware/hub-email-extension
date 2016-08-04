package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.factory.AbstractEmailFactory;

public abstract class AbstractPollingDispatcher extends TimerTask {

	private final Logger logger = LoggerFactory.getLogger(AbstractPollingDispatcher.class);
	public static long DEFAULT_POLLING_INTERVAL = 10000;
	public static long DEFAULT_POLLING_DELAY = 5000;

	private Timer timer;
	private long interval;
	private long startupDelay;
	private final Map<String, List<AbstractEmailFactory>> topicSubscriberMap = new ConcurrentHashMap<>();
	private ExecutorService executorService;

	private Date lastRun;
	private Date currentRun;
	private String name;

	public AbstractPollingDispatcher() {
		interval = DEFAULT_POLLING_INTERVAL;
		startupDelay = DEFAULT_POLLING_DELAY;
	}

	public void start() {
		String startMsg = "Started";
		if (StringUtils.isNotBlank(getName())) {
			startMsg += ": " + getName();
		} else {
			startMsg += " polling for messages";
		}
		logger.info(startMsg);
		stopTimer();
		timer = new Timer();
		timer.schedule(this, startupDelay, interval);
	}

	public void stop() {
		stopTimer();
		shutdown();
		String stopMsg = "Stopped";
		if (StringUtils.isNotBlank(getName())) {
			stopMsg += ": " + getName();
		} else {
			stopMsg += " polling for messages";
		}
		logger.info(stopMsg);
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void attachRouter(final AbstractEmailFactory router) {
		final List<AbstractEmailFactory> routerList = new Vector<>();
		routerList.add(router);
		attachRouters(routerList);
	}

	public void attachRouters(final List<AbstractEmailFactory> routers) {
		for (final AbstractEmailFactory factory : routers) {
			final Set<String> topics = factory.getSubscriberTopics();
			for (final String topic : topics) {
				List<AbstractEmailFactory> factoryList;
				if (topicSubscriberMap.containsKey(topic)) {
					factoryList = topicSubscriberMap.get(topic);
				} else {
					factoryList = new Vector<>();
				}
				topicSubscriberMap.put(topic, factoryList);
			}
		}
	}

	public void unattachRouter(final AbstractEmailFactory router) {
		final List<AbstractEmailFactory> factoryList = new ArrayList<>();
		factoryList.add(router);
		unattachRouters(factoryList);
	}

	public void unattachRouters(final List<AbstractEmailFactory> routerFactories) {
		for (final AbstractEmailFactory factory : routerFactories) {
			final Set<String> topics = factory.getSubscriberTopics();
			for (final String topic : topics) {
				if (topicSubscriberMap.containsKey(topic)) {
					final List<AbstractEmailFactory> factoryList = topicSubscriberMap.get(topic);
					factoryList.remove(factory);
					if (logger.isDebugEnabled()) {
						logger.debug("Router removed: " + factory);
						logger.debug("Current router count: " + topicSubscriberMap.size());
					}
					if (factoryList.isEmpty()) {
						topicSubscriberMap.remove(topic);
					}
				}
			}
		}
	}

	private void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		// cleanup topics router map
		if (executorService != null) {
			executorService.shutdown();
		}
		final Set<String> factoryTopics = topicSubscriberMap.keySet();
		for (final String topic : factoryTopics) {
			final List<AbstractEmailFactory> factoryList = topicSubscriberMap.get(topic);
			final Iterator<AbstractEmailFactory> iterator = factoryList.iterator();
			while (iterator.hasNext()) {
				iterator.remove();
			}
		}
		topicSubscriberMap.clear(); // remove all topics
	}

	@Override
	public void run() {
		currentRun = new Date();
		final Map<String, EmailTaskData> dataMap = fetchData();
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Execution data: " + System.lineSeparator() + "########## Polling Dispatcher Execution ##########"
							+ System.lineSeparator() + "Dispatcher Name  = " + name + System.lineSeparator()
							+ "Polling interval = " + interval + System.lineSeparator() + "Last Run         = "
							+ lastRun + System.lineSeparator() + "Current Run      = " + currentRun
							+ System.lineSeparator() + "##################################################");
		}
		final Set<String> factoryTopics = dataMap.keySet();
		for (final String topic : factoryTopics) {
			if (topicSubscriberMap.containsKey(topic)) {
				final List<AbstractEmailFactory> factoryList = topicSubscriberMap.get(topic);
				for (final AbstractEmailFactory routerFactory : factoryList) {
					final AbstractEmailRouter<?> router = routerFactory.createInstance(dataMap.get(topic));
					executorService.submit(router);
				}
			}
		}
		lastRun = currentRun;
	}

	public Date getCurrentRun() {
		return currentRun;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(final long interval) {
		this.interval = interval;
	}

	public long getStartupDelay() {
		return startupDelay;
	}

	public void setStartupDelay(final long startupDelay) {
		this.startupDelay = startupDelay;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(final ExecutorService executorService) {
		this.executorService = executorService;
	}

	public abstract void init();

	public abstract Map<String, EmailTaskData> fetchData();
}
