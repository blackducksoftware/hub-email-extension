package com.blackducksoftware.integration.email.messaging.events;

public class ReceiveEventDispatcher<D> extends AbstractEventDispatcher<ReceiveMessageListener<D>, D> {

	@Override
	public Runnable createEventTask(final ReceiveMessageListener<D> listener, final D topicEventData) {
		return new ReceieveEventTask(listener, topicEventData);
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
