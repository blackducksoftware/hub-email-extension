package com.blackducksoftware.integration.email.messaging;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.blackducksoftware.integration.email.messaging.events.MessageEvent;
import com.blackducksoftware.integration.email.messaging.events.MessageEventDispatcher;
import com.blackducksoftware.integration.email.messaging.events.MessageEventListener;

public abstract class AbstractPollingConsumer<M> extends TimerTask {

	public static long DEFAULT_POLLING_INTERVAL = 10000;

	private Timer timer;
	private final long interval;
	private final MessageEventDispatcher<M> eventDispatcher = new MessageEventDispatcher<M>();

	public AbstractPollingConsumer() {
		interval = DEFAULT_POLLING_INTERVAL;
	}

	public void start() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new Timer();
		timer.schedule(this, 0, interval);
	}

	public void stop() {
		timer.cancel();
		eventDispatcher.shutdown();
	}

	public void addListener(final MessageEventListener<M> listener) {
		eventDispatcher.addListener(listener);
	}

	public void removeListener(final MessageEventListener<M> listener) {
		eventDispatcher.removeListener(listener);
	}

	public abstract List<M> fetchMessages();

	@Override
	public void run() {
		final List<M> messageList = fetchMessages();
		if (messageList != null && !messageList.isEmpty()) {
			// send event
			final MessageEvent<M> event = new MessageEvent<M>(messageList);
			eventDispatcher.dispatchEvent(event);
		}
	}
}