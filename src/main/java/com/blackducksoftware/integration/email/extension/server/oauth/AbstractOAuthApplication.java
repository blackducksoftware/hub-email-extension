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

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.blackducksoftware.integration.email.extension.server.oauth.resources.ClientIdRegistrationResource;
import com.blackducksoftware.integration.email.extension.server.oauth.resources.OAuthConfigurationResource;
import com.blackducksoftware.integration.email.extension.server.oauth.resources.TokenAuthenticationResource;
import com.blackducksoftware.integration.email.extension.server.oauth.resources.TokenCallbackResource;

public abstract class AbstractOAuthApplication extends Application {

    private final ExtensionTokenManager tokenManager;

    public AbstractOAuthApplication(final ExtensionTokenManager tokenManager) {
        super();
        this.tokenManager = tokenManager;
    }

    @Override
    public Restlet createInboundRoot() {
        getContext().getAttributes().put(ExtensionTokenManager.CONTEXT_ATTRIBUTE_KEY, tokenManager);
        additionalContextConfig();
        final Router router = new Router(getContext());
        router.attach(OAuthServerConstants.REGISTRATION, ClientIdRegistrationResource.class);
        router.attach(OAuthServerConstants.EXTENSION_CONFIG, OAuthConfigurationResource.class);
        router.attach(OAuthServerConstants.CALLBACK, TokenCallbackResource.class);
        router.attach(OAuthServerConstants.AUTH_GRANT, TokenAuthenticationResource.class);
        additionalRouterConfig(router);
        return router;
    }

    public abstract void additionalContextConfig();

    public abstract void additionalRouterConfig(final Router router);

}
