package com.blackducksoftware.integration.email.messaging;

public abstract class ItemRouter<C, R, S> implements RouterSubscriber {

	public abstract void configure(C data);

	public abstract void receive(R data);

	public abstract void send(final S data);
}
