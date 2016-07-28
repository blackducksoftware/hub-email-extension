package com.blackducksoftware.integration.email.messaging.events;

import java.util.Set;

public interface ConfigureMessageListener<D> {
	public Set<String> getConfigureEventTopics();

	public void configure(D data);
}
