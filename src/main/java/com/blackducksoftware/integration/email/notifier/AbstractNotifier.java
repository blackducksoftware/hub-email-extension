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
package com.blackducksoftware.integration.email.notifier;

import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.dataservice.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.model.view.ExternalExtensionConfigValueView;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.Slf4jIntLogger;

public abstract class AbstractNotifier extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(AbstractNotifier.class);

    private final ExtensionProperties extensionProperties;

    private final EmailMessagingService emailMessagingService;

    private String hubExtensionUri;

    private final HubServicesFactory hubServicesFactory;

    private final ExtensionConfigDataService extensionConfigDataService;

    private final ExtensionInfo extensionInfo;

    public AbstractNotifier(final ExtensionProperties extensionProperties,
            final EmailMessagingService emailMessagingService, final HubServicesFactory hubServicesFactory, final ExtensionInfo extensionInfo) {
        this.extensionProperties = extensionProperties;
        this.emailMessagingService = emailMessagingService;
        this.hubServicesFactory = hubServicesFactory;
        this.extensionInfo = extensionInfo;
        extensionConfigDataService = hubServicesFactory.createExtensionConfigDataService(new Slf4jIntLogger(logger));

    }

    public ExtensionProperties createPropertiesFromGlobalConfig() throws IntegrationException {
        final Map<String, ExternalExtensionConfigValueView> globalMap = extensionConfigDataService
                .getGlobalConfigMap(getHubExtensionUri());
        final Properties globalProperties = new Properties();
        for (final Map.Entry<String, ExternalExtensionConfigValueView> entry : globalMap.entrySet()) {
            globalProperties.put(entry.getKey(), entry.getValue().value.get(0));
        }
        return new ExtensionProperties(globalProperties);
    }

    public abstract String getTemplateName();

    public abstract String getCronExpression();

    public abstract String getNotifierPropertyKey();

    public long getStartDelayMilliseconds() {
        return 0;
    }

    public ExtensionProperties getExtensionProperties() {
        return extensionProperties;
    }

    public EmailMessagingService getEmailMessagingService() {
        return emailMessagingService;
    }

    public String getName() {
        return getClass().getName();
    }

    public String getHubExtensionUri() {
        return hubExtensionUri;
    }

    public void setHubExtensionUri(final String hubExtensionUri) {
        this.hubExtensionUri = hubExtensionUri;
    }

    public ExtensionConfigDataService getExtensionConfigDataService() {
        return extensionConfigDataService;
    }

    public HubServicesFactory getHubServicesFactory() {
        return hubServicesFactory;
    }

    public ExtensionInfo getExtensionInfoData() {
        return extensionInfo;
    }
}
