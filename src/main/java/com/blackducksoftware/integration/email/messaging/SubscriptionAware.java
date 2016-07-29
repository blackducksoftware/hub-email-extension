package com.blackducksoftware.integration.email.messaging;

import java.util.Set;

public interface SubscriptionAware {
	public abstract Set<String> getTopics();
}
