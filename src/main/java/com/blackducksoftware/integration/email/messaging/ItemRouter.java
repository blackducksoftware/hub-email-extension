package com.blackducksoftware.integration.email.messaging;

import com.blackducksoftware.integration.email.messaging.events.ConfigureMessageListener;
import com.blackducksoftware.integration.email.messaging.events.ReceiveMessageListener;

public abstract class ItemRouter<C, R, S> implements ConfigureMessageListener<C>, ReceiveMessageListener<R> {
	public abstract void send(final S data);
}
