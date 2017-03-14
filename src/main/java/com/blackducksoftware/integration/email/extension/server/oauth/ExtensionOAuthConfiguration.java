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

import com.blackducksoftware.integration.hub.api.oauth.OAuthConfiguration;

public class ExtensionOAuthConfiguration extends OAuthConfiguration {

    public String hubUri;

    public String extensionUri;

    public ExtensionOAuthConfiguration() {
    }

    public boolean isClientConfigured() {
        return clientId != null;
    }

    public void setAddresses(final String hubUri, final String extensionUri, final String oAuthAuthorizeUri,
            final String oAuthTokenUri) {
        this.hubUri = hubUri;
        this.extensionUri = extensionUri;
        this.authorizeUri = oAuthAuthorizeUri;
        this.tokenUri = oAuthTokenUri;
    }
}
