package com.blackducksoftware.integration.email.messaging.events;

public interface MessageEventListener<M> {

	public void receive(MessageEvent<M> event);
}
