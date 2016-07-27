package com.blackducksoftware.integration.email.messaging;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.messaging.events.MessageEvent;
import com.blackducksoftware.integration.email.messaging.events.MessageEventDispatcher;
import com.blackducksoftware.integration.email.messaging.events.MessageEventListener;

public abstract class AbstractPollingDispatcher<M> extends TimerTask {

	private static Logger logger = LoggerFactory.getLogger(AbstractPollingDispatcher.class);
	public static long DEFAULT_POLLING_INTERVAL = 10000;
	public static long DEFAULT_POLLING_DELAY = 5000;

	private Timer timer;
	private final long interval;
	private final MessageEventDispatcher<M> eventDispatcher = new MessageEventDispatcher<M>();

	private Date lastRun;
	private Date currentRun;

	public AbstractPollingDispatcher() {
		interval = DEFAULT_POLLING_INTERVAL;
	}

	public void start() {
		logger.info("Start polling for messages.");
		stopTimer();
		timer = new Timer();
		timer.schedule(this, DEFAULT_POLLING_DELAY, interval);
	}

	public void stop() {
		stopTimer();
		eventDispatcher.shutdown();
		logger.info("Stopped polling for messages.");
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void addListener(final MessageEventListener<M> listener) {
		logger.debug("Registering listener: " + listener);
		eventDispatcher.addListener(listener);
	}

	public void removeListener(final MessageEventListener<M> listener) {
		logger.debug("Unregistering listener: " + listener);
		eventDispatcher.removeListener(listener);
	}

	public abstract List<M> fetchMessages();

	@Override
	public void run() {
		currentRun = new Date();
		logger.debug("########## Polling Dispatcher Execution ##########");
		logger.debug("Polling interval= " + interval);
		logger.debug("Last Run        =  " + lastRun);
		logger.debug("Current Run     = " + currentRun);
		final List<M> messageList = fetchMessages();
		logger.debug("Message List to dispatch: " + messageList);
		if (messageList != null && !messageList.isEmpty()) {
			// send event
			final MessageEvent<M> event = new MessageEvent<M>(messageList);
			eventDispatcher.dispatchEvent(event);
		}
		lastRun = currentRun;
		logger.debug("");
		logger.debug("##################################################");
	}

	public Date getCurrentRun() {
		return currentRun;
	}

	public Date getLastRun() {
		return lastRun;
	}
}
