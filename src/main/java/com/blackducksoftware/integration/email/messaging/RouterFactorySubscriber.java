package com.blackducksoftware.integration.email.messaging;

import java.util.Set;

public interface RouterFactorySubscriber {
	public Set<String> getSubscriberTopics();
}
