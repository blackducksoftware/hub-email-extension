package com.blackducksoftware.integration.email.service;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import com.blackducksoftware.integration.email.dto.ConfigResponse;
import com.blackducksoftware.integration.email.dto.ConfigurationElement;
import com.google.gson.Gson;

public class ConfigurationElementParser {
	public static void test(final String[] args) throws IOException {
		final ConfigurationElementParser parser = new ConfigurationElementParser();
		final ClassPathResource configResponseResource = new ClassPathResource("hub_ui_config_response.json");
		final String json = IOUtils.toString(configResponseResource.getInputStream());
		final ConfigResponse configResponse = parser.fromJson(json);
		for (final ConfigurationElement element : configResponse.getConfigurationElements()) {
			System.out.println(element.getName());
		}
	}

	public ConfigResponse fromJson(final String json) {
		final Gson gson = new Gson();
		final ConfigResponse configResponse = gson.fromJson(json, ConfigResponse.class);
		return configResponse;
	}

}
