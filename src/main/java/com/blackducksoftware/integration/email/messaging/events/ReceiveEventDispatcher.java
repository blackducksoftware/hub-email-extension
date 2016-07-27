package com.blackducksoftware.integration.email.messaging.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveEventDispatcher<D> extends AbstractEventDispatcher<ReceiveMessageListener<D>, D> {

	private final static Logger logger = LoggerFactory.getLogger(ReceiveEventDispatcher.class);

	@Override
	public void dispatchEvent(final D data) {
		// execute in a thread for each listener.
		for (final ReceiveMessageListener<D> listener : getListenerList()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Dispatching to listener: " + listener + " data: " + data);
			}
			final ReceieveEventTask task = new ReceieveEventTask(listener, data);
			submitEvent(task);
		}
	}

	private class ReceieveEventTask implements Runnable {

		private final ReceiveMessageListener<D> listener;
		private final D data;

		public ReceieveEventTask(final ReceiveMessageListener<D> listener, final D data) {
			this.listener = listener;
			this.data = data;
		}

		@Override
		public void run() {
			listener.receive(data);
		}
	}
}
