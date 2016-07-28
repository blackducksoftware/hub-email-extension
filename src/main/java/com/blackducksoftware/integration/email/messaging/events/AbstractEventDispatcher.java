package com.blackducksoftware.integration.email.messaging.events;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventDispatcher<L, E> {
	private final Logger logger = LoggerFactory.getLogger(AbstractEventDispatcher.class);
	private final Map<String, List<L>> topicListenerMap = new ConcurrentHashMap<>();
	private final ExecutorService eventExecutor;

	public AbstractEventDispatcher() {
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		eventExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	public void addListener(final Set<String> topics, final L listener) {
		if (topics != null) {
			for (final String topic : topics) {
				List<L> listeners;
				if (topicListenerMap.containsKey(topic)) {
					listeners = topicListenerMap.get(topic);
				} else {
					listeners = new Vector<L>();
					topicListenerMap.put(topic, listeners);
				}
				listeners.add(listener);
				if (logger.isDebugEnabled()) {
					logger.debug("Listener added: " + listener);
					logger.debug("Topic: " + topic + " current Listener Count: " + listeners.size());
				}
			}
		}
	}

	public void removeListener(final Set<String> topics, final L listener) {
		if (topics != null) {
			for (final String topic : topics) {
				int listenerCount = 0;
				if (topicListenerMap.containsKey(topic)) {
					final List<L> listeners = topicListenerMap.get(topic);
					listeners.remove(listener);
					listenerCount = listeners.size();
					if (listeners.isEmpty()) {
						topicListenerMap.remove(topic);
					}
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Listener removed: " + listener);
					logger.debug("Topic: " + topic + " current Listener Count: " + listenerCount);
				}
			}
		}
	}

	public void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		// cleanup topics listener map
		eventExecutor.shutdown();
		final Set<String> topicSet = topicListenerMap.keySet();

		for (final String topic : topicSet) {
			final List<L> listeners = topicListenerMap.get(topic);
			final Iterator<L> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				final L listener = iterator.next();
				iterator.remove();
				logger.info("Listener removed: " + listener);
				logger.info("Topic: " + topic + " current Listener Count: " + listeners.size());
			}
			topicListenerMap.remove(topic);
		}
		topicListenerMap.clear(); // remove all topics
	}

	public Map<String, List<L>> getTopicListenerMap() {
		return topicListenerMap;
	}

	// expect this method to be called in the implementation of the
	// dispatchEvent method.
	public void submitEvent(final Runnable runnable) {
		eventExecutor.submit(runnable);
	}

	public void dispatchEvent(final Map<String, E> data) {
		// execute in a thread for each listener.
		final Set<String> topicSet = data.keySet();
		for (final String topic : topicSet) {
			if (getTopicListenerMap().containsKey(topic)) {
				final List<L> listenerList = getTopicListenerMap().get(topic);
				for (final L listener : listenerList) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								"Dispatching for topic: " + topic + " to listener: " + listener + " event: " + data);
					}
					final E topicEventData = data.get(topic);
					final Runnable task = createEventTask(listener, topicEventData);
					submitEvent(task);
				}
			}
		}
	}

	public abstract Runnable createEventTask(L listener, E topicEventData);
}
