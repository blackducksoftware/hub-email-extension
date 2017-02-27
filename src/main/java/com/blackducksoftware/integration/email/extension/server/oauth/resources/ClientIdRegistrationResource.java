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

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

import com.blackducksoftware.integration.email.extension.server.oauth.ExtensionTokenManager;

public class ClientIdRegistrationResource extends OAuthServerResource {

    @Post
    public void accept(final JsonRepresentation entity) {
        final String clientId = entity.getJsonObject().getString("clientId");

        if (StringUtils.isNotBlank(clientId)) {
            final ExtensionTokenManager tokenManager = getTokenManager();
            if (tokenManager != null) {
                getTokenManager().updateClientId(clientId);
            } else {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No client ID/hub URL provided");
        }
    }
}
