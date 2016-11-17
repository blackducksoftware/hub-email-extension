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

import java.util.Optional;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.server.oauth.StateUrlProcessor;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;

public class TokenAuthenticationResource extends OAuthServerResource {

    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationResource.class);

    @Get
    public void authenticate() {
        // Use state if provided
        final String next = getRequest().getResourceRef().getQueryAsForm(true).getFirstValue("next");
        final StateUrlProcessor state = new StateUrlProcessor(getQueryValue("state"));

        if (state.getReturnUrl().isPresent() && next != null) {
            state.setReturnUrl(next);
        } else if (getRequest().getReferrerRef() != null) {
            state.setReturnUrl(getRequest().getReferrerRef().toString());
        }
        final TokenManager tokenManager = getTokenManager();
        if (tokenManager != null) {
            logger.info("Authenticate method called to obtain authorization url");
            final Reference authUrl = new Reference(tokenManager.getOAuthAuthorizationUrl(Optional.of(state)));
            getResponse().redirectSeeOther(authUrl);
        } else {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }
}
