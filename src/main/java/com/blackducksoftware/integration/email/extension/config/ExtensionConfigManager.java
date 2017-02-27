/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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

    public static final String PROPERTY_KEY_CONFIG_LOCATION_PATH = "ext.config.location";

    private final ExtensionInfo extensionInfo;

    private final JsonParser parser;

    public ExtensionConfigManager(final ExtensionInfo extensionInfo, final JsonParser parser) {
        this.extensionInfo = extensionInfo;
        this.parser = parser;
    }

    public ExtensionInfo getExtensionInfo() {
        return extensionInfo;
    }

    public String loadGlobalConfigJSON() {
        final String configLocation = System.getProperty(PROPERTY_KEY_CONFIG_LOCATION_PATH);
        final File globalConfig = new File(configLocation, "config-options.json");
        logger.info("Reading extension global configuration descriptor file {}", globalConfig);
        if (!globalConfig.exists()) {
            return "";
        } else {
            try (FileReader reader = new FileReader(globalConfig)) {
                final String jsonString = createJSonString(reader);
                jsonString.replaceAll("null", ""); // remove any null strings
                logger.debug("global config descriptor: {}", jsonString);
                return jsonString;
            } catch (final IOException e) {
                logger.error("Error reading global config file config-options.json");
                return "";
            }
        }
    }

    public String loadUserConfigJSON() {
        final String configLocation = System.getProperty(PROPERTY_KEY_CONFIG_LOCATION_PATH);
        final File userConfig = new File(configLocation, "user-config-options.json");
        logger.info("Reading extension user configuration descriptor file {}", userConfig);
        if (!userConfig.exists()) {
            return "";
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
        final JsonElement element = parser.parse(reader);
        final JsonArray array = element.getAsJsonArray();
        final JsonObject object = new JsonObject();
        object.addProperty("totalCount", array.size());
        object.add("items", array);
        return object.toString();
    }
}
