package com.blackducksoftware.integration.email.messaging.events;

import java.util.List;

public class MessageEvent<M> {

	private final List<M> messageList;

	public MessageEvent(final List<M> messageList) {
		if (messageList == null) {
			throw new IllegalArgumentException("message list cannot be null");
		}
		this.messageList = messageList;
	}

	public List<M> getMessageList() {
		return this.messageList;
	}
}
