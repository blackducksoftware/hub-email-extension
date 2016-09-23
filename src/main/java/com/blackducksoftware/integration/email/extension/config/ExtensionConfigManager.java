package com.blackducksoftware.integration.email.extension.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExtensionConfigManager {
	private final Logger logger = LoggerFactory.getLogger(ExtensionConfigManager.class);

	public static final String CONTEXT_ATTRIBUTE_KEY = "blackduck-extension-config-manager";
	private static final String CONFIG_LOCATION_PATH = "ext.config.location";

	private final ExtensionInfo extensionInfo;

	public ExtensionConfigManager(final ExtensionInfo extensionInfo) {
		this.extensionInfo = extensionInfo;
	}

	public ExtensionInfo getExtensionInfo() {
		return extensionInfo;
	}

	public String loadGlobalConfigJSON() {
		final String configLocation = System.getProperty(CONFIG_LOCATION_PATH);
		final File globalConfig = new File(configLocation, "config-options.json");
		logger.info("Reading extension global configuration descriptor file {}", globalConfig);
		if (!globalConfig.exists()) {
			return "";
		} else {
			try (FileReader reader = new FileReader(globalConfig)) {
				final String jsonString = createJSonString(reader);
				logger.debug("global config descriptor: {}", jsonString);
				return jsonString;
			} catch (final IOException e) {
				logger.error("Error reading global config file config-options.json");
				return "";
			}
		}
	}

	public String loadUserConfigJSON() {
		final String configLocation = System.getProperty(CONFIG_LOCATION_PATH);
		final File userConfig = new File(configLocation, "user-config-options.json");
		logger.info("Reading extension user configuration descriptor file {}", userConfig);
		if (!userConfig.exists()) {
			return loadGlobalConfigJSON();
		} else {
			try (FileReader reader = new FileReader(userConfig)) {
				final String jsonString = createJSonString(reader);
				logger.debug("user config descriptor: {}", jsonString);
				return jsonString;
			} catch (final IOException e) {
				logger.error("Error reading global config file config-options.json");
				return "";
			}
		}
	}

	public String createJSonString(final Reader reader) {
		final JsonParser parser = new JsonParser();
		final JsonElement element = parser.parse(reader);
		final JsonArray array = element.getAsJsonArray();
		final JsonObject object = new JsonObject();
		object.addProperty("totalCount", array.size());
		object.add("items", array);
		return object.toString();
	}
}
