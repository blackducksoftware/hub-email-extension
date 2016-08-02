package com.blackducksoftware.integration.email.messaging;

public class RouterTaskData<D> {

	private final D data;

	public RouterTaskData(final D data) {
		this.data = data;
	}

	public D getData() {
		return data;
	}
}
