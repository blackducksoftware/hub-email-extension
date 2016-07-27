package com.blackducksoftware.integration.email.messaging.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigureEventDispatcher<D> extends AbstractEventDispatcher<ConfigureMessageListener<D>, D> {

	private final static Logger logger = LoggerFactory.getLogger(ConfigureEventDispatcher.class);

	@Override
	public void dispatchEvent(final D data) {
		// execute in a thread for each listener.
		for (final ConfigureMessageListener<D> listener : getListenerList()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Dispatching to listener: " + listener + " event: " + data);
			}
			final ConfigureEventTask task = new ConfigureEventTask(listener, data);
			submitEvent(task);
		}
	}

	private class ConfigureEventTask implements Runnable {

		private final ConfigureMessageListener<D> listener;
		private final D data;

		public ConfigureEventTask(final ConfigureMessageListener<D> listener, final D data) {
			this.listener = listener;
			this.data = data;
		}

		@Override
		public void run() {
			listener.configure(data);
		}
	}
}
