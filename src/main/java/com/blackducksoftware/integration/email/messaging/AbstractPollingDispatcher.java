package com.blackducksoftware.integration.email.messaging;

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

public abstract class AbstractPollingDispatcher<D> extends TimerTask {

	private final Logger logger = LoggerFactory.getLogger(AbstractPollingDispatcher.class);
	public static long DEFAULT_POLLING_INTERVAL = 10000;
	public static long DEFAULT_POLLING_DELAY = 5000;

	private Timer timer;
	private long interval;
	private long startupDelay;
	private final Map<String, List<ItemRouterFactory<D>>> topicSubscriberMap = new ConcurrentHashMap<>();
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

	public void attachRouter(final ItemRouterFactory<D> router) {
		final List<ItemRouterFactory<D>> routerList = new Vector<>();
		routerList.add(router);
		attachRouters(routerList);
	}

	public void attachRouters(final List<ItemRouterFactory<D>> routers) {
		for (final ItemRouterFactory<D> factory : routers) {
			final Set<String> topics = factory.getSubscriberTopics();
			for (final String topic : topics) {
				List<ItemRouterFactory<D>> factoryList;
				if (topicSubscriberMap.containsKey(topic)) {
					factoryList = topicSubscriberMap.get(topic);
				} else {
					factoryList = new Vector<>();
				}
				topicSubscriberMap.put(topic, factoryList);
			}
		}
	}

	public void unattachRouter(final ItemRouterFactory<D> router) {
		final List<ItemRouterFactory<D>> factoryList = new ArrayList<>();
		factoryList.add(router);
		unattachRouters(factoryList);
	}

	public void unattachRouters(final List<ItemRouterFactory<D>> routerFactories) {
		for (final ItemRouterFactory<D> factory : routerFactories) {
			final Set<String> topics = factory.getSubscriberTopics();
			for (final String topic : topics) {
				if (topicSubscriberMap.containsKey(topic)) {
					final List<ItemRouterFactory<D>> factoryList = topicSubscriberMap.get(topic);
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
			final List<ItemRouterFactory<D>> factoryList = topicSubscriberMap.get(topic);
			final Iterator<ItemRouterFactory<D>> iterator = factoryList.iterator();
			while (iterator.hasNext()) {
				iterator.remove();
			}
		}
		topicSubscriberMap.clear(); // remove all topics
	}

	@Override
	public void run() {
		currentRun = new Date();
		final Map<String, RouterTaskData<D>> dataMap = fetchRouterConfig();
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
				final List<ItemRouterFactory<D>> factoryList = topicSubscriberMap.get(topic);
				for (final ItemRouterFactory<D> routerFactory : factoryList) {
					final ItemRouter<D> router = routerFactory.createInstance(dataMap.get(topic));
					if (logger.isDebugEnabled()) {
						logger.debug("Dispatching to router " + router.getName());
					}
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

	public abstract Map<String, RouterTaskData<D>> fetchRouterConfig();
}
