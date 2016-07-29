package com.blackducksoftware.integration.email.messaging;

import java.util.Set;

public interface RouterSubscriber {
	public abstract Set<String> getTopics();
}
