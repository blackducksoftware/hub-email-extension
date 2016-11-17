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
package com.blackducksoftware.integration.email.extension.server.resources;

import java.util.List;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.extension.server.ExtensionServerConstants;
import com.blackducksoftware.integration.email.extension.server.api.model.ExtensionDescriptor;
import com.blackducksoftware.integration.email.extension.server.api.model.ResourceLink;
import com.blackducksoftware.integration.email.extension.server.api.model.ResourceMetadata;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthServerConstants;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.google.common.collect.Lists;

public class ExtensionInfoServerResource extends ExtensionServerResource {

    @Get("json")
    public ExtensionDescriptor represent() {
        final ExtensionConfigManager extConfigManager = getExtensionConfigManager();
        final TokenManager tokenManager = getTokenManager();
        if (extConfigManager != null && tokenManager != null) {
            final ExtensionInfo extensionInfo = extConfigManager.getExtensionInfo();
            final ResourceMetadata metadata = buildMetadata(extensionInfo, tokenManager);
            return new ExtensionDescriptor(extensionInfo.getName(), extensionInfo.getDescription(),
                    extensionInfo.getId(), metadata);
        } else {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            return null;
        }
    }

    private String buildAddress(final ExtensionInfo extensionInfo, final String path) {
        final Reference ref = new Reference(extensionInfo.getBaseUrl());
        ref.setPath(path);
        return ref.toString();
    }

    public ResourceMetadata buildMetadata(final ExtensionInfo extensionInfo, final TokenManager tokenManager) {
        final String configAddress = buildAddress(extensionInfo, OAuthServerConstants.EXTENSION_CONFIG);
        final String clientConfigAddress = buildAddress(extensionInfo, OAuthServerConstants.REGISTRATION);
        final String authAddress = buildAddress(extensionInfo, OAuthServerConstants.AUTH_GRANT);
        final String callbackAddress = buildAddress(extensionInfo, OAuthServerConstants.CALLBACK);
        // update tokenManager with the callback address
        tokenManager.updateCallbackUrl(callbackAddress);
        // extension configuration paths.
        final String infoAddress = buildAddress(extensionInfo, ExtensionServerConstants.EXTENSION_INFO);
        final String globalOptionsAddress = buildAddress(extensionInfo, ExtensionServerConstants.GLOBAL_CONFIG_VALUES);
        final String userOptionsAddress = buildAddress(extensionInfo, ExtensionServerConstants.USER_CONFIG_VALUES);

        final List<ResourceLink> links = Lists.newArrayList();
        links.add(new ResourceLink("extension-config", configAddress.toString()));
        links.add(new ResourceLink("client-config", clientConfigAddress.toString()));
        links.add(new ResourceLink("extension-authenticate", authAddress.toString()));
        links.add(new ResourceLink("global-config-options", globalOptionsAddress.toString()));
        links.add(new ResourceLink("user-config-options", userOptionsAddress.toString()));
        links.add(new ResourceLink("oauth-callback", callbackAddress.toString()));

        return new ResourceMetadata(infoAddress.toString(), links);
    }
}
