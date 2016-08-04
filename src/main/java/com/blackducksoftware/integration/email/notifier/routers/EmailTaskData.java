package com.blackducksoftware.integration.email.notifier.routers;

import java.util.List;

public class EmailTaskData {
	private final List<Object> data;

	public EmailTaskData(final List<Object> data) {
		this.data = data;
	}

	public List<Object> getData() {
		return data;
	}

}
