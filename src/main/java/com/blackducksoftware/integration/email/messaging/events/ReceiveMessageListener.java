package com.blackducksoftware.integration.email.messaging.events;

import java.util.Set;

public interface ReceiveMessageListener<D> {

	public Set<String> getReceiveEventTopics();

	public void receive(D data);
}
