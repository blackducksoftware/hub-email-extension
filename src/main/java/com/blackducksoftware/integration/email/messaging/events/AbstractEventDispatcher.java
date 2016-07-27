package com.blackducksoftware.integration.email.messaging.events;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventDispatcher<L, E> {
	private final static Logger logger = LoggerFactory.getLogger(AbstractEventDispatcher.class);
	private final List<L> listenerList = new Vector<L>();
	private final ExecutorService eventExecutor;

	public AbstractEventDispatcher() {
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		eventExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	public void addListener(final L listener) {
		logger.debug("Listener added: " + listener);
		listenerList.add(listener);
		logger.debug("Current Listener Count: " + listenerList.size());
	}

	public void removeListener(final L listener) {
		logger.debug("Listener removed: " + listener);
		listenerList.remove(listener);
		logger.debug("Current Listener Count: " + listenerList.size());
	}

	public void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		eventExecutor.shutdown();
	}

	public List<L> getListenerList() {
		return listenerList;
	}

	// expect this method to be called in the implementation of the
	// dispatchEvent method.
	public void submitEvent(final Runnable runnable) {
		eventExecutor.submit(runnable);
	}

	public abstract void dispatchEvent(final E data);

}
