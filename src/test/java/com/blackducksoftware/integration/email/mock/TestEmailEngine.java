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
package com.blackducksoftware.integration.email.mock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailEngine;
import com.blackducksoftware.integration.email.extension.server.oauth.ExtensionTokenManager;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.NotifierManager;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.Credentials;
import com.blackducksoftware.integration.hub.dataservice.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.proxy.ProxyInfoBuilder;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;

public class TestEmailEngine extends EmailEngine {

    private final Logger logger = LoggerFactory.getLogger(TestEmailEngine.class);

    public TestEmailEngine() throws FileNotFoundException, IOException {
        super();
    }

    @Override
    public RestConnection createRestConnection(final String hubUri) {
        try {
            return new MockRestConnection(new MockLogger(), new URL(hubUri));
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtensionTokenManager createTokenManager() {
        final ExtensionTokenManager tokenManager = super.createTokenManager();
        tokenManager.setAddresses("http://localhost:8080", "http://localhost:8100", "", "");
        return tokenManager;
    }

    @Override
    public HubServerConfig createHubConfig(final String hubUri) {
        HubServerConfig serverConfig = null;
        try {
            Credentials credentials;
            credentials = new Credentials("user", "password");

            final ProxyInfo proxyInfo = new ProxyInfoBuilder().build();
            serverConfig = new HubServerConfig(new URL("http://localhost"), 120, credentials, proxyInfo, false);
        } catch (final EncryptionException | MalformedURLException e) {
            logger.error("Error creating hub server config", e);
        }
        return serverConfig;
    }

    @Override
    public JavaMailWrapper createJavaMailWrapper() {
        return new MockMailWrapper(false);
    }

    @Override
    public NotificationDataService createNotificationDataService() {
        HubServicesFactory hubServicesFactory;
        hubServicesFactory = new HubServicesFactory(getRestConnection());
        final IntLogger hubLogger = new Slf4jIntLogger(logger);
        return new MockNotificationDataService(hubLogger, hubServicesFactory.createHubResponseService(), hubServicesFactory.createNotificationRequestService(), hubServicesFactory.createProjectVersionRequestService(),
                hubServicesFactory.createPolicyRequestService(), hubServicesFactory.createMetaService());

    }

    @Override
    public NotifierManager createNotifierManager() {
        final NotifierManager manager = new NotifierManager();
        final TestDigestNotifier digestNotifier = new TestDigestNotifier(getExtensionProperties(), getEmailMessagingService(), getHubServicesFactory(), getExtensionInfoData());
        manager.attach(digestNotifier);
        return manager;
    }

    @Override
    public void start() {
        onAuthorized(); // finish initialization
    }
}
