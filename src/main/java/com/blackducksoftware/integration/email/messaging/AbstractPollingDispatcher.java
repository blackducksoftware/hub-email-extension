package com.blackducksoftware.integration.email.messaging;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
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
	private final List<ItemRouterFactory<D>> topicSubscriberMap = new Vector<>();
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
		topicSubscriberMap.addAll(routers);
	}

	public void unattachRouter(final ItemRouter<D> router) {
		topicSubscriberMap.remove(router);
	}

	public void unattachRouters(final List<ItemRouter<D>> routers) {
		for (final ItemRouter<D> router : routers) {
			topicSubscriberMap.remove(router);
			if (logger.isDebugEnabled()) {
				logger.debug("Router removed: " + router);
				logger.debug("Current router count: " + topicSubscriberMap.size());
			}
		}
	}

	private void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		// cleanup topics router map
		if (executorService != null) {
			executorService.shutdown();
		}
		topicSubscriberMap.clear(); // remove all topics
	}

	@Override
	public void run() {
		currentRun = new Date();
		final RouterTaskData<D> data = fetchRouterConfig();
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Execution data: " + System.lineSeparator() + "########## Polling Dispatcher Execution ##########"
							+ System.lineSeparator() + "Dispatcher Name  = " + name + System.lineSeparator()
							+ "Polling interval = " + interval + System.lineSeparator() + "Last Run         = "
							+ lastRun + System.lineSeparator() + "Current Run      = " + currentRun
							+ System.lineSeparator() + "##################################################");
		}

		for (final ItemRouterFactory<D> routerFactory : topicSubscriberMap) {
			final ItemRouter<D> router = routerFactory.createInstance(data);
			if (logger.isDebugEnabled()) {
				logger.debug("Dispatching to router " + router.getName());
			}

			executorService.submit(router);
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

	public abstract RouterTaskData<D> fetchRouterConfig();
}
