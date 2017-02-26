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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.extension.server.oauth.listeners.IAuthorizedListener;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.oauth.Token;
import com.blackducksoftware.integration.hub.rest.oauth.AccessType;
import com.blackducksoftware.integration.hub.rest.oauth.OAuthRestConnection;
import com.blackducksoftware.integration.log.IntLogger;
import com.google.gson.JsonObject;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TokenManager extends com.blackducksoftware.integration.hub.rest.oauth.TokenManager {

    private final Logger logger = LoggerFactory.getLogger(TokenManager.class);

    public final static String CONTEXT_ATTRIBUTE_KEY = "blackduck-oauth-token-manager";

    public static final String EXTENSIONS_URL_IDENTIFIER = "externalextensions";

    private final ExtensionOAuthConfiguration configuration;

    private final ExtensionInfo extensionInfo;

    private final List<IAuthorizedListener> authorizedListeners;

    private final OAuthConfigManager configManager;

    private Token userToken;

    public TokenManager(final IntLogger logger, final int timeout, final ExtensionInfo extensionInfo) {
        super(logger, timeout);
        configManager = new OAuthConfigManager();
        configuration = configManager.load();
        setConfiguration(configuration);
        this.extensionInfo = extensionInfo;
        authorizedListeners = new ArrayList<>();
    }

    @Override
    public ExtensionOAuthConfiguration getConfiguration() {
        return configuration;
    }

    public OAuthConfigManager getConfigManager() {
        return configManager;
    }

    public boolean authenticationRequired() {
        return userToken == null;
    }

    public String getLocalAddress() {
        return extensionInfo.getBaseUrl();
    }

    public void updateCallbackUrl(final String callbackUrl) {
        getConfiguration().callbackUrl = callbackUrl;
    }

    public void updateClientId(final String clientId) {
        getConfiguration().clientId = clientId;
    }

    public void setAddresses(final String hubUri, final String extensionUri, final String oAuthAuthorizeUri,
            final String oAuthTokenUri) {
        logger.debug("Received hub addresses hubUri: {}, extensionUri: {}, oAuthAuthorizeUri: {}, oAuthTokenUri: {}",
                hubUri, extensionUri, oAuthAuthorizeUri, oAuthTokenUri);
        configuration.setAddresses(hubUri, extensionUri, oAuthAuthorizeUri, oAuthTokenUri);
    }

    public String getOAuthAuthorizationUrl(final Optional<StateUrlProcessor> state) {
        return configManager.getOAuthAuthorizationUrl(configuration, state);
    }

    public boolean addAuthorizedListener(final IAuthorizedListener listener) {
        return authorizedListeners.add(listener);
    }

    public boolean removeAuthorizedListener(final IAuthorizedListener listener) {
        return authorizedListeners.remove(listener);
    }

    public void notifyAuthorizedListeners() {
        for (final IAuthorizedListener listener : authorizedListeners) {
            listener.onAuthorized();
        }
    }

    public void exchangeForToken(final String authorizationCode) throws IntegrationException, MalformedURLException {
        userToken = this.exchangeForUserToken(authorizationCode);
        completeAuthorization();
    }

    public void completeAuthorization() throws MalformedURLException, IntegrationException {
        // Update authorization status
        // this is hub specific as far as I can tell to send status for
        // the authorization.
        updateAuthorized(true);

        configuration.setUserRefreshToken(userToken.refreshToken);
        configManager.persist(configuration);
        notifyAuthorizedListeners();

    }

    private void updateAuthorized(final boolean authorized) throws IntegrationException, MalformedURLException {
        try {
            final URL url = new URL(getConfiguration().getExtensionUri());
            final OAuthRestConnection connection = new OAuthRestConnection(getLogger(), url, getTimeout(), this, AccessType.CLIENT);
            final HttpUrl httpUrl = connection.createHttpUrl();
            final Request request = connection.createGetRequest(httpUrl);
            // submit the data to the hub
            final Response response = connection.handleExecuteClientCall(request);
            final JsonObject responseBody = connection.getGson().fromJson(response.body().string(), JsonObject.class);
            responseBody.addProperty("authenticated", Boolean.TRUE);
            final String content = connection.getGson().toJson(responseBody);
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
            final Request putRequest = connection.createPutRequest(httpUrl, requestBody);
            connection.handleExecuteClientCall(putRequest);
        } catch (final IOException ex) {
            throw new IntegrationException("Couldn't update authenticated property of extension", ex);
        }
    }
}
