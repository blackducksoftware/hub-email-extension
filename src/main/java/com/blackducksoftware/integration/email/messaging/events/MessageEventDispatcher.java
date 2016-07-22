package com.blackducksoftware.integration.email.messaging.events;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MessageEventDispatcher<M> {

	private final List<MessageEventListener<M>> listenerList = new Vector<MessageEventListener<M>>();
	private final ExecutorService eventExecutor;

	public MessageEventDispatcher() {
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		eventExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	public void addListener(final MessageEventListener<M> listener) {
		listenerList.add(listener);
	}

	public void removeListener(final MessageEventListener<M> listener) {
		listenerList.remove(listener);
	}

	public void dispatchEvent(final MessageEvent<M> event) {
		// execute in a thread for each listener.
		for (final MessageEventListener<M> listener : listenerList) {
			final ReceieveEventTask task = new ReceieveEventTask(listener, event);
			eventExecutor.submit(task);
		}
	}

	public void shutdown() {
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
