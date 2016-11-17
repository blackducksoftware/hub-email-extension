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

import com.blackducksoftware.integration.email.ExtensionLogger;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.extension.ConfigurationItem;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;

public abstract class AbstractNotifier extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(AbstractNotifier.class);

    private final ExtensionProperties extensionProperties;

    private final EmailMessagingService emailMessagingService;

    private final DataServicesFactory dataServicesFactory;

    private String hubExtensionUri;

    private final ExtensionConfigDataService extensionConfigDataService;

    public AbstractNotifier(final ExtensionProperties extensionProperties,
            final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory) {
        this.extensionProperties = extensionProperties;
        this.emailMessagingService = emailMessagingService;
        this.dataServicesFactory = dataServicesFactory;
        final ExtensionLogger extLogger = new ExtensionLogger(logger);
        extensionConfigDataService = dataServicesFactory.createExtensionConfigDataService(extLogger);
    }

    public ExtensionProperties createPropertiesFromGlobalConfig() throws UnexpectedHubResponseException {
        final Map<String, ConfigurationItem> globalMap = extensionConfigDataService
                .getGlobalConfigMap(getHubExtensionUri());
        final Properties globalProperties = new Properties();
        for (final Map.Entry<String, ConfigurationItem> entry : globalMap.entrySet()) {
            globalProperties.put(entry.getKey(), entry.getValue().getValue().get(0));
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

    public DataServicesFactory getDataServicesFactory() {
        return dataServicesFactory;
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
}
