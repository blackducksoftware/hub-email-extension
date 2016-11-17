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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.oauth.AccessTokenClientResource;
import org.restlet.ext.oauth.OAuthException;
import org.restlet.ext.oauth.internal.Token;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.extension.server.oauth.listeners.IAuthorizedListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class TokenManager {

    private final Logger logger = LoggerFactory.getLogger(TokenManager.class);

    public final static String CONTEXT_ATTRIBUTE_KEY = "blackduck-oauth-token-manager";

    public static final String EXTENSIONS_URL_IDENTIFIER = "externalextensions";

    private final OAuthConfiguration configuration;

    // Internal storage for tokens - done in-memory as a simple example
    private Token userToken = null;

    private Token clientToken = null;

    private final ExtensionInfo extensionInfo;

    private final List<IAuthorizedListener> authorizedListeners;

    private final OAuthConfigManager configManager;

    public TokenManager(final ExtensionInfo extensionInfo) {
        configManager = new OAuthConfigManager();
        configuration = configManager.load();
        this.extensionInfo = extensionInfo;
        authorizedListeners = new ArrayList<>();
    }

    public OAuthConfiguration getConfiguration() {
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
        configuration.setCallbackUrl(callbackUrl);
    }

    public void updateClientId(final String clientId) {
        configuration.setClientId(clientId);
    }

    public void setAddresses(final String hubUri, final String extensionUri, final String oAuthAuthorizeUri,
            final String oAuthTokenUri) {
        logger.info("Received hub addresses hubUri: {}, extensionUri: {}, oAuthAuthorizeUri: {}, oAuthTokenUri: {}",
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

    public void exchangeForToken(final String authorizationCode) throws IOException {
        final AccessTokenClientResource tokenResource = configManager.getTokenResource(configuration);
        try {
            userToken = tokenResource
                    .requestToken(configManager.getAccessTokenParameters(configuration, authorizationCode));
            completeAuthorization();
        } catch (JSONException | OAuthException | URISyntaxException e) {
            throw new IOException(e);
        }
    }

    public void refreshToken(final AccessType accessType) throws IOException {
        if (AccessType.USER.equals(accessType)) {
            refreshUserAccessToken();
        } else if (AccessType.CLIENT.equals(accessType)) {
            refreshClientAccessToken();
        }
    }

    public TokenClientResource createClientResource(final String reference, final AccessType accessType)
            throws IOException, URISyntaxException {
        final Token token = getToken(accessType);
        return new TokenClientResource(new URI(reference), token);
    }

    public void completeAuthorization() throws IOException, URISyntaxException {
        // Update authorization status
        // this is hub specific as far as I can tell to send status for
        // the authorization.
        final String hubExtensionUri = getConfiguration().getExtensionUri();
        final TokenClientResource resource = createClientResource(hubExtensionUri, AccessType.CLIENT);
        try {
            updateAuthorized(resource);
        } catch (final ResourceException e) {
            if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(e.getStatus())) {
                // Try one more time, after refreshing tokens
                refreshToken(AccessType.CLIENT);
                updateAuthorized(resource);
            } else {
                throw e;
            }
        }
        configuration.setUserRefreshToken(userToken.getRefreshToken());
        configManager.persist(configuration);
        notifyAuthorizedListeners();
    }

    private Token getToken(final AccessType accessType) throws IOException {
        Token result = null;

        if (AccessType.USER.equals(accessType)) {
            if (userToken == null) {
                throw new IllegalStateException("User token not populated");
            } else {
                result = userToken;
            }
        } else if (AccessType.CLIENT.equals(accessType)) {
            if (clientToken == null) {
                refreshClientAccessToken();
            }
            result = clientToken;
        }

        return result;
    }

    private void refreshUserAccessToken() throws IOException {
        if (StringUtils.isNotBlank(configuration.getUserRefreshToken())) {
            final AccessTokenClientResource tokenResource = configManager.getTokenResource(configuration);
            try {
                userToken = tokenResource
                        .requestToken(configManager.getRefreshTokenParameters(configuration.getUserRefreshToken()));
                completeAuthorization();
            } catch (JSONException | OAuthException | URISyntaxException e) {
                throw new IOException(e);
            }
        } else {
            throw new IllegalStateException("No token present to refresh");
        }
    }

    private void refreshClientAccessToken() throws IOException {
        final AccessTokenClientResource tokenResource = configManager.getTokenResource(configuration);
        try {
            clientToken = tokenResource.requestToken(configManager.getClientTokenParameters());
        } catch (JSONException | OAuthException e) {
            throw new IOException(e);
        }
    }

    private void updateAuthorized(final ClientResource resource) throws IOException {
        final Representation rep = resource.get();
        final JsonParser parser = new JsonParser();
        try {
            logger.info("Updating hub of authentication status");
            final JsonElement json = parser.parse(rep.getText());
            json.getAsJsonObject().add("authenticated", new JsonPrimitive(true));

            resource.put(new JsonRepresentation(json.toString()));
        } catch (final IOException e) {
            throw e;
        }
    }
}
