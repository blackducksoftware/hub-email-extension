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
package com.blackducksoftware.integration.email.extension.server.oauth.resources;

import java.net.MalformedURLException;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import com.blackducksoftware.integration.email.extension.server.oauth.StateUrlProcessor;
import com.blackducksoftware.integration.email.extension.server.oauth.ExtensionTokenManager;
import com.blackducksoftware.integration.exception.IntegrationException;

public class TokenCallbackResource extends OAuthServerResource {

    @Get
    public void accept() {
        final ExtensionTokenManager tokenManager = getTokenManager();
        if (tokenManager != null) {
            final String authorizationCode = getQuery().getFirstValue("code");
            final String urlState = getQuery().getFirstValue("state");

            final StateUrlProcessor state = new StateUrlProcessor(urlState);
            final Reference redirectTo;

            if (state.getReturnUrl().isPresent()) {
                redirectTo = new Reference(state.getReturnUrl().get());
            } else {
                redirectTo = new Reference(tokenManager.getLocalAddress());
            }

            try {
                tokenManager.exchangeForToken(authorizationCode);
                getResponse().redirectSeeOther(redirectTo);
            } catch (final IntegrationException | MalformedURLException e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            }
        } else {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, "No token manager available");
        }
    }
}
