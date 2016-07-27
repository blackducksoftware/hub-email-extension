package com.blackducksoftware.integration.email.messaging.events;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageEventDispatcher<M> {

	private final static Logger logger = LoggerFactory.getLogger(MessageEventDispatcher.class);
	private final List<MessageEventListener<M>> listenerList = new Vector<MessageEventListener<M>>();
	private final ExecutorService eventExecutor;

	public MessageEventDispatcher() {
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		eventExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	public void addListener(final MessageEventListener<M> listener) {
		logger.debug("Listener added: " + listener);
		listenerList.add(listener);
		logger.debug("Current Listener Count: " + listenerList.size());
	}

	public void removeListener(final MessageEventListener<M> listener) {
		logger.debug("Listener removed: " + listener);
		listenerList.remove(listener);
		logger.debug("Current Listener Count: " + listenerList.size());
	}

	public void dispatchEvent(final MessageEvent<M> event) {
		// execute in a thread for each listener.
		for (final MessageEventListener<M> listener : listenerList) {
			if (logger.isDebugEnabled()) {
				logger.debug("Dispatching to listener: " + listener + " event: " + event);
			}
			final ReceieveEventTask task = new ReceieveEventTask(listener, event);
			eventExecutor.submit(task);
		}
	}

	public void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		eventExecutor.shutdown();
	}

	private class ReceieveEventTask implements Runnable {

		private final MessageEventListener<M> listener;
		private final MessageEvent<M> event;

		public ReceieveEventTask(final MessageEventListener<M> listener, final MessageEvent<M> event) {
			this.listener = listener;
			this.event = event;
		}

		@Override
		public void run() {
			listener.receive(event);
		}
	}
}
