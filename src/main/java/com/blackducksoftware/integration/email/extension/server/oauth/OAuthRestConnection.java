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
package com.blackducksoftware.integration.email.extension.server.oauth;

import java.io.IOException;
import java.net.URL;

import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubProxyInfo;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.log.IntLogger;

public class OAuthRestConnection extends RestConnection {

    private final TokenManager tokenManager;

    public OAuthRestConnection(URL baseUrl, HubProxyInfo proxyInfo, TokenManager tokenManager) {
        super(baseUrl);
        this.tokenManager = tokenManager;
    }

    public OAuthRestConnection(IntLogger logger, URL baseUrl, HubProxyInfo proxyInfo, TokenManager tokenManager) {
        super(logger, baseUrl, proxyInfo);
        this.tokenManager = tokenManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    @Override
    public void addBuilderAuthentication() throws HubIntegrationException {

    }

    @Override
    public void clientAuthenticate() throws HubIntegrationException {
        try {
            tokenManager.refreshToken(AccessType.USER);
        } catch (final IOException e) {
            throw new HubIntegrationException("Error connecting to the Hub server.", e);
        }
    }
}
