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

public class OAuthConfiguration {
    private String clientId;

    private String callbackUrl;

    private String hubUri;

    private String extensionUri;

    private String oAuthAuthorizeUri;

    private String oAuthTokenUri;

    private String userRefreshToken;

    public OAuthConfiguration() {
    }

    public boolean isClientConfigured() {
        return clientId != null;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getHubUri() {
        return hubUri;
    }

    public void setHubUri(final String hubUri) {
        this.hubUri = hubUri;
    }

    public String getExtensionUri() {
        return extensionUri;
    }

    public void setExtensionUri(final String extensionUri) {
        this.extensionUri = extensionUri;
    }

    public String getoAuthAuthorizeUri() {
        return oAuthAuthorizeUri;
    }

    public void setoAuthAuthorizeUri(final String oAuthAuthorizeUri) {
        this.oAuthAuthorizeUri = oAuthAuthorizeUri;
    }

    public String getoAuthTokenUri() {
        return oAuthTokenUri;
    }

    public void setoAuthTokenUri(final String oAuthTokenUri) {
        this.oAuthTokenUri = oAuthTokenUri;
    }

    public String getUserRefreshToken() {
        return userRefreshToken;
    }

    public void setUserRefreshToken(final String userRefreshToken) {
        this.userRefreshToken = userRefreshToken;
    }

    public void setAddresses(final String hubUri, final String extensionUri, final String oAuthAuthorizeUri,
            final String oAuthTokenUri) {
        this.hubUri = hubUri;
        this.extensionUri = extensionUri;
        this.oAuthAuthorizeUri = oAuthAuthorizeUri;
        this.oAuthTokenUri = oAuthTokenUri;
    }
}
