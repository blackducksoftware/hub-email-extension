package com.blackducksoftware.integration.email.model;

import java.util.List;
import java.util.Map;

public class EmailData {

	private final List<String> addresses;
	private final Map<String, Object> model;

	public EmailData(final List<String> addresses, final Map<String, Object> model) {
		this.addresses = addresses;
		this.model = model;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public Map<String, Object> getModel() {
		return model;
	}
}
