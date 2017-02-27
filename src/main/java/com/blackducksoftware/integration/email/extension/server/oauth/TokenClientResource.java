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

import java.net.URI;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.ext.oauth.OAuthResourceDefs;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.oauth.Token;
import com.blackducksoftware.integration.hub.rest.oauth.AccessType;

public class TokenClientResource extends ClientResource implements OAuthResourceDefs {

    private final AccessType accessType;

    private final TokenManager tokenManager;

    public TokenClientResource(final URI uri, final TokenManager tokenManager, final AccessType accessType) {
        super(uri);
        this.tokenManager = tokenManager;
        this.accessType = accessType;
    }

    @Override
    public Response handleOutbound(final Request request) {
        Response response;
        Token token;
        try {
            token = tokenManager.getToken(accessType);
            if (token.tokenType.equalsIgnoreCase(TOKEN_TYPE_BEARER)) {
                final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
                cr.setRawValue(token.accessToken);
                request.setChallengeResponse(cr);
            } else {
                throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "Unsupported token type.");
            }

            response = super.handleOutbound(request);

            if (!isSuccess(response.getStatus().getCode())) {
                tokenManager.refreshToken(accessType);
                token = tokenManager.getToken(accessType);
                final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
                cr.setRawValue(token.accessToken);
                request.setChallengeResponse(cr);
                response = super.handleOutbound(request);
            }
        } catch (final IntegrationException ex) {
            try {
                tokenManager.refreshToken(accessType);
                token = tokenManager.getToken(accessType);
            } catch (final IntegrationException e) {
                throw new RuntimeException(e);
            }
            final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
            cr.setRawValue(token.accessToken);
            request.setChallengeResponse(cr);
            response = super.handleOutbound(request);
        }

        return response;
    }

    public boolean isSuccess(final int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }
}
