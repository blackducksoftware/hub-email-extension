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
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.Context;
import org.restlet.resource.ClientResource;

import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class OAuthRestConnection extends RestConnection {

    private final TokenManager tokenManager;

    public OAuthRestConnection(final HubServerConfig hubServerConfig, final TokenManager tokenManager) {
        setBaseUrl(hubServerConfig.getHubUrl().toString());
        setProxyProperties(hubServerConfig.getProxyInfo());
        setTimeout(hubServerConfig.getTimeout());
        this.tokenManager = tokenManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    @Override
    public ClientResource createClientResource(final Context context, final String providedUrl) throws HubIntegrationException {
        try {
            return tokenManager.createClientResource(providedUrl, AccessType.USER);
        } catch (final IOException | URISyntaxException ex) {
            try {
                return new ClientResource(context, new URI(providedUrl));
            } catch (final URISyntaxException uriEx) {
                throw new HubIntegrationException(uriEx);
            }
        }
    }

    @Override
    public void connect() throws HubIntegrationException {
        try {
            tokenManager.refreshToken(AccessType.USER);
        } catch (final IOException e) {
            throw new HubIntegrationException("Error connecting to the Hub server.", e);
        }
    }
}
