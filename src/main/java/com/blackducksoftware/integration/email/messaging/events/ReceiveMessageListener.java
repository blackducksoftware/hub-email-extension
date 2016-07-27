package com.blackducksoftware.integration.email.messaging.events;

public interface ReceiveMessageListener<D> {

	public void receive(D data);
}
