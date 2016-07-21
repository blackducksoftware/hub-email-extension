package com.blackducksoftware.integration.email.dto;

import java.util.List;

public class ConfigurationResponse {
	private List<ConfigurationElement> configurationElements;

	public List<ConfigurationElement> getConfigurationElements() {
		return configurationElements;
	}

	public void setConfigurationElements(final List<ConfigurationElement> configurationElements) {
		this.configurationElements = configurationElements;
	}

}
