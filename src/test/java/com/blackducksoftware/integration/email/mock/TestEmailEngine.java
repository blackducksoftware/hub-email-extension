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
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.NotifierManager;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubProxyInfoBuilder;
import com.blackducksoftware.integration.hub.dataservice.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservice.notification.item.PolicyNotificationFilter;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubCredentials;
import com.blackducksoftware.integration.hub.global.HubProxyInfo;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.Slf4jIntLogger;

public class TestEmailEngine extends EmailEngine {

    private final Logger logger = LoggerFactory.getLogger(TestEmailEngine.class);

    public TestEmailEngine() throws FileNotFoundException, IOException {
        super();
    }

    @Override
    public RestConnection createRestConnection(String hubUri) {
        return new MockRestConnection();
    }

    @Override
    public TokenManager createTokenManager() {
        final TokenManager tokenManager = super.createTokenManager();
        tokenManager.setAddresses("http://localhost:8080", "http://localhost:8100", "", "");
        return tokenManager;
    }

    @Override
    public HubServerConfig createHubConfig(final String hubUri) {
        HubServerConfig serverConfig = null;
        try {
            HubCredentials credentials;
            credentials = new HubCredentials("user", "password");

            final HubProxyInfo proxyInfo = new HubProxyInfoBuilder().buildResults().getConstructedObject();
            serverConfig = new HubServerConfig(new URL("http://localhost"), 120, credentials, proxyInfo);
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
        try {
            hubServicesFactory = new HubServicesFactory(getRestConnection());

            return new MockNotificationDataService(new Slf4jIntLogger(logger), getRestConnection(), hubServicesFactory.createNotificationRequestService(),
                    hubServicesFactory.createProjectVersionRequestService(), hubServicesFactory.createPolicyRequestService(),
                    hubServicesFactory.createVersionBomPolicyRequestService(), hubServicesFactory.createHubRequestService(),
                    new PolicyNotificationFilter(null));
        } catch (final HubIntegrationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotifierManager createNotifierManager() {
        final NotifierManager manager = new NotifierManager();
        try {
            final HubServicesFactory hubServicesFactory = new HubServicesFactory(getRestConnection());
            final TestDigestNotifier digestNotifier = new TestDigestNotifier(getExtensionProperties(), getEmailMessagingService(),
                    hubServicesFactory.createHubRequestService(), hubServicesFactory.createVulnerabilityRequestService(), getExtConfigDataService(),
                    hubServicesFactory.createNotificationDataService(new Slf4jIntLogger(logger)));
            manager.attach(digestNotifier);
        } catch (final HubIntegrationException e) {

        }
        return manager;
    }

    @Override
    public void start() {
        onAuthorized(); // finish initialization
    }
}
