package com.blackducksoftware.integration.email.messaging;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPollingDispatcher<D, F extends RouterSubscriber> extends TimerTask {

	private final Logger logger = LoggerFactory.getLogger(AbstractPollingDispatcher.class);
	public static long DEFAULT_POLLING_INTERVAL = 10000;
	public static long DEFAULT_POLLING_DELAY = 5000;

	private Timer timer;
	private long interval;
	private long startupDelay;
	private final Map<String, List<F>> topicSubscriberMap = new ConcurrentHashMap<>();
	private final ExecutorService eventExecutor;

	private Date lastRun;
	private Date currentRun;
	private String name;

	public AbstractPollingDispatcher() {
		interval = DEFAULT_POLLING_INTERVAL;
		startupDelay = DEFAULT_POLLING_DELAY;
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		eventExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);

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

	public void attachRouters(final List<F> routers) {
		for (final F router : routers) {
			for (final String topic : router.getTopics()) {
				List<F> routerList;
				if (topicSubscriberMap.containsKey(topic)) {
					routerList = topicSubscriberMap.get(topic);
				} else {
					routerList = new Vector<F>();
					topicSubscriberMap.put(topic, routerList);
				}
				routerList.add(router);
				if (logger.isDebugEnabled()) {
					logger.debug("Router added: " + router);
					logger.debug("Topic: " + topic + " current subscriber count: " + routers.size());
				}
			}
		}
	}

	public void unattachRouters(final List<F> routers) {
		for (final F router : routers) {
			for (final String topic : router.getTopics()) {
				int routerCount = 0;
				if (topicSubscriberMap.containsKey(topic)) {
					final List<F> routerList = topicSubscriberMap.get(topic);
					routerList.remove(router);
					routerCount = routerList.size();
					if (routerList.isEmpty()) {
						topicSubscriberMap.remove(topic);
					}
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Router removed: " + router);
					logger.debug("Topic: " + topic + " current subscriber count: " + routerCount);
				}
			}
		}
	}

	private void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		// cleanup topics router map
		eventExecutor.shutdown();
		final Set<String> topicSet = topicSubscriberMap.keySet();

		for (final String topic : topicSet) {
			final List<F> routers = topicSubscriberMap.get(topic);
			final Iterator<F> iterator = routers.iterator();
			while (iterator.hasNext()) {
				final F router = iterator.next();
				iterator.remove();
				logger.info("Router removed: " + router);
				logger.info("Topic: " + topic + " current subscriber count: " + routers.size());
			}
			topicSubscriberMap.remove(topic);
		}
		topicSubscriberMap.clear(); // remove all topics
	}

	public void dispatchEvent(final Map<String, D> data) {
		final Set<String> topicSet = data.keySet();
		for (final String topic : topicSet) {
			if (topicSubscriberMap.containsKey(topic)) {
				final List<F> routerList = topicSubscriberMap.get(topic);
				for (final F router : routerList) {
					if (logger.isDebugEnabled()) {
						logger.debug("Dispatching to subscriber" + router + " for topic: " + topic + " data: " + data);
					}
					// execute the processing of the data in a separate thread
					// in the threadpool
					final Runnable task = createEventTask(router, data.get(topic));
					eventExecutor.submit(task);
				}
			}
		}
	}

	@Override
	public void run() {
		currentRun = new Date();
		final Map<String, D> data = fetchData();
		if (logger.isDebugEnabled()) {
			logger.debug("Execution data: " + System.lineSeparator()
					+ "########## Polling Dispatcher Execution ##########" + System.lineSeparator()
					+ "Dispatcher Name  = " + name + System.lineSeparator() + "Polling interval = " + interval
					+ System.lineSeparator() + "Last Run         = " + lastRun + System.lineSeparator()
					+ "Current Run      = " + currentRun + System.lineSeparator() + "Data to dispatch " + data
					+ System.lineSeparator() + "##################################################");
		}
		dispatchEvent(data);
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

	public abstract void initDispatcher();

	public abstract Map<String, D> fetchData();

	public abstract Runnable createEventTask(F router, D data);
}
