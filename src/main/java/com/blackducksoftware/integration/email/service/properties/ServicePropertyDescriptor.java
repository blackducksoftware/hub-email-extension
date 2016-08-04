package com.blackducksoftware.integration.email.service.properties;

public enum ServicePropertyDescriptor {
	HUB_SERVER_URL("hub.server.url", "http://ahub.customer1.com"),
	HUB_USER("hub.server.user", "user"),
	HUB_PASSWORD("hub.server.password", "password"),
	HUB_TIMEOUT("hub.server.timeout", "120"),
	HUB_PROXY_HOST("hub.proxy.host", ""),
	HUB_PROXY_PORT("hub.proxy.port", ""),
	HUB_PROXY_USER("hub.proxy.user", ""),
	HUB_PROXY_PASS("hub.proxy.password", ""),
	HUB_PROXY_NO_HOST("hub.proxy.nohost", "");

	private final String key;
	private final String defaultValue;

	private ServicePropertyDescriptor(final String key, final String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
