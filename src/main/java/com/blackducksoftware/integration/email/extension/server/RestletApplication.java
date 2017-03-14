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
package com.blackducksoftware.integration.email.extension.server;

import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.extension.server.oauth.AbstractOAuthApplication;
import com.blackducksoftware.integration.email.extension.server.oauth.ExtensionTokenManager;
import com.blackducksoftware.integration.email.extension.server.resources.ExtensionInfoServerResource;
import com.blackducksoftware.integration.email.extension.server.resources.GlobalConfigServerResource;
import com.blackducksoftware.integration.email.extension.server.resources.UserConfigServerResource;

public class RestletApplication extends AbstractOAuthApplication {

    private final ExtensionConfigManager extConfigManager;

    public RestletApplication(final ExtensionTokenManager tokenManager, final ExtensionConfigManager extConfigManager) {
        super(tokenManager);
        this.extConfigManager = extConfigManager;
    }

    @Override
    public void additionalContextConfig() {
        getContext().getAttributes().put(ExtensionConfigManager.CONTEXT_ATTRIBUTE_KEY, extConfigManager);
    }

    @Override
    public void additionalRouterConfig(final Router router) {
        router.attach("/",
                new Redirector(getContext(), ExtensionServerConstants.EXTENSION_INFO, Redirector.MODE_CLIENT_FOUND));

        router.attach(ExtensionServerConstants.EXTENSION_INFO, ExtensionInfoServerResource.class);
        router.attach(ExtensionServerConstants.GLOBAL_CONFIG_VALUES, GlobalConfigServerResource.class);
        router.attach(ExtensionServerConstants.USER_CONFIG_VALUES, UserConfigServerResource.class);
    }
}
