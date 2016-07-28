package com.blackducksoftware.integration.email.messaging.events;

public class ConfigureEventDispatcher<D> extends AbstractEventDispatcher<ConfigureMessageListener<D>, D> {

	@Override
	public Runnable createEventTask(final ConfigureMessageListener<D> listener, final D topicEventData) {
		return new ConfigureEventTask(listener, topicEventData);
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
