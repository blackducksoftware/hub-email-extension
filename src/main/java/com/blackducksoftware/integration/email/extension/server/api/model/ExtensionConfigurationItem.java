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
package com.blackducksoftware.integration.email.extension.server.api.model;

import org.json.JSONObject;

public class ExtensionConfigurationItem {
    private String extensionUrl;

    private String hubBaseUrl;

    private String oAuthAuthorizeUrl;

    private String oAuthTokenUrl;

    public ExtensionConfigurationItem() {
    }

    public ExtensionConfigurationItem(final JSONObject json) {
        this(json.getString("extensionUrl"), json.getString("hubBaseUrl"), json.getString("oAuthAuthorizeUrl"),
                json.getString("oAuthTokenUrl"));
    }

    public ExtensionConfigurationItem(final String extensionUrl, final String hubBaseUrl,
            final String oAuthAuthorizeUrl, final String oAuthTokenUrl) {
        this.extensionUrl = extensionUrl;
        this.hubBaseUrl = hubBaseUrl;
        this.oAuthAuthorizeUrl = oAuthAuthorizeUrl;
        this.oAuthTokenUrl = oAuthTokenUrl;
    }

    public String getExtensionUrl() {
        return extensionUrl;
    }

    public void setExtensionUrl(final String extensionUrl) {
        this.extensionUrl = extensionUrl;
    }

    public String getHubBaseUrl() {
        return hubBaseUrl;
    }

    public void setHubBaseUrl(final String hubBaseUrl) {
        this.hubBaseUrl = hubBaseUrl;
    }

    public String getoAuthAuthorizeUrl() {
        return oAuthAuthorizeUrl;
    }

    public void setoAuthAuthorizeUrl(final String oAuthAuthorizeUrl) {
        this.oAuthAuthorizeUrl = oAuthAuthorizeUrl;
    }

    public String getoAuthTokenUrl() {
        return oAuthTokenUrl;
    }

    public void setoAuthTokenUrl(final String oAuthTokenUrl) {
        this.oAuthTokenUrl = oAuthTokenUrl;
    }
}
