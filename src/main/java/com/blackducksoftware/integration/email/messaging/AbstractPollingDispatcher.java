package com.blackducksoftware.integration.email.messaging;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.messaging.events.AbstractEventDispatcher;

public abstract class AbstractPollingDispatcher<L, D> extends TimerTask {

	private static Logger logger = LoggerFactory.getLogger(AbstractPollingDispatcher.class);
	public static long DEFAULT_POLLING_INTERVAL = 10000;
	public static long DEFAULT_POLLING_DELAY = 5000;

	private Timer timer;
	private long interval;
	private long startupDelay;
	private AbstractEventDispatcher<L, D> eventDispatcher;

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
		if (eventDispatcher != null) {

			timer = new Timer();
			timer.schedule(this, startupDelay, interval);
		} else {
			String errorMsg = "Event dispatcher";

			if (StringUtils.isNotBlank(getName())) {
				errorMsg += "for dispatcher " + getName();
			}

			errorMsg += " is null!";
			logger.error(errorMsg);
			logger.error("Dispatcher not started");
		}
	}

	public void stop() {
		stopTimer();
		if (eventDispatcher != null) {
			eventDispatcher.shutdown();
		}
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

	public void addListener(final L listener) {
		logger.debug("Registering listener: " + listener);
		eventDispatcher.addListener(listener);
	}

	public void removeListener(final L listener) {
		logger.debug("Unregistering listener: " + listener);
		eventDispatcher.removeListener(listener);
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

	public AbstractEventDispatcher<L, D> getEventDispatcher() {
		return eventDispatcher;
	}

	public void setEventDispatcher(final AbstractEventDispatcher<L, D> eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
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

	public abstract D createEventData();

	@Override
	public void run() {
		currentRun = new Date();
		final Date previousRun = lastRun;

		final D data = createEventData();

		if (data != null) {
			eventDispatcher.dispatchEvent(data);
		}
		lastRun = currentRun;
		if (logger.isDebugEnabled()) {
			logger.debug("Execution data: " + System.lineSeparator()
					+ "########## Polling Dispatcher Execution ##########" + System.lineSeparator()
					+ "Dispatcher Name  = " + name + System.lineSeparator() + "Polling interval = " + interval
					+ System.lineSeparator() + "Last Run         = " + previousRun + System.lineSeparator()
					+ "Current Run      = " + currentRun + System.lineSeparator() + "Data to dispatch " + data
					+ System.lineSeparator() + "##################################################");
		}
	}
}
