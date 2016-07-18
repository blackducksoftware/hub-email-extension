package com.blackducksoftware.integration.email.dto;

public class ConfigResponse {
	private ConfigurationElement[] configurationElements;

	public ConfigurationElement[] getConfigurationElements() {
		return configurationElements;
	}

	public void setConfigurationElements(final ConfigurationElement[] configurationElements) {
		this.configurationElements = configurationElements;
	}

}
