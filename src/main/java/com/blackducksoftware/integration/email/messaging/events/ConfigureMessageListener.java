package com.blackducksoftware.integration.email.messaging.events;

public interface ConfigureMessageListener<D> {
	public void configure(D data);
}
