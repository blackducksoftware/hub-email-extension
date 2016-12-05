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
package com.blackducksoftware.integration.email.model;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.builder.ValidationResults;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.global.GlobalFieldKey;
import com.blackducksoftware.integration.hub.global.HubServerConfig;

public class HubServerBeanConfiguration {
    private final Logger logger = LoggerFactory.getLogger(HubServerBeanConfiguration.class);

    private final String hubUri;

    private final ExtensionProperties emailConfig;

    public HubServerBeanConfiguration(final String hubUri, final ExtensionProperties emailConfig) {
        this.emailConfig = emailConfig;
        this.hubUri = hubUri;
    }

    public HubServerConfig build() {
        final HubServerConfigBuilder configBuilder = new HubServerConfigBuilder();
        configBuilder.setHubUrl(hubUri);
        // using oauth the username and password aren't used but need to be set
        // for the builder
        configBuilder.setUsername("auser");
        configBuilder.setPassword("apassword");
        configBuilder.setTimeout(emailConfig.getHubServerTimeout());
        configBuilder.setProxyHost(emailConfig.getHubProxyHost());
        configBuilder.setProxyPort(emailConfig.getHubProxyPort());
        configBuilder.setIgnoredProxyHosts(emailConfig.getHubProxyNoHost());
        configBuilder.setProxyUsername(emailConfig.getHubProxyUser());
        configBuilder.setProxyPassword(emailConfig.getHubProxyPassword());

        // output the configuration details
        logger.info("Hub Server URL          = " + configBuilder.getHubUrl());
        logger.info("Hub Timeout             = " + configBuilder.getTimeout());
        logger.info("Hub Proxy Host          = " + configBuilder.getProxyHost());
        logger.info("Hub Proxy Port          = " + configBuilder.getProxyPort());
        logger.info("Hub Ignored Proxy Hosts = " + configBuilder.getIgnoredProxyHosts());
        logger.info("Hub Proxy User          = " + configBuilder.getProxyUsername());

        final ValidationResults<GlobalFieldKey, HubServerConfig> results = configBuilder.buildResults();

        if (results.hasErrors()) {
            logger.error("##### Properties file contains errors.####");
            final Set<GlobalFieldKey> keys = results.getResultMap().keySet();
            for (final GlobalFieldKey fieldKey : keys) {
                logger.error(results.getResultString(fieldKey));
            }
        } else {
            if (results.hasWarnings()) {
                final Set<GlobalFieldKey> keys = results.getResultMap().keySet();
                for (final GlobalFieldKey fieldKey : keys) {
                    logger.warn(results.getResultString(fieldKey));
                }
            }
            return results.getConstructedObject();
        }

        return null;
    }

}
