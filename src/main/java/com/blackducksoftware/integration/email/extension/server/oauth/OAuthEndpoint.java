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

import org.restlet.Component;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.security.AuthenticatorHelper;

public class OAuthEndpoint extends Component {

    public OAuthEndpoint(final AbstractOAuthApplication application) {

        getDefaultHost().attachDefault(application);

        getClients().add(Protocol.FILE);
        getClients().add(Protocol.HTTP);
        getClients().add(Protocol.HTTPS);
        // Prevent warnings about unsupported authentication
        Engine.getInstance().getRegisteredAuthenticators()
                .add(new AuthenticatorHelper(ChallengeScheme.HTTP_OAUTH_BEARER, true, false) {
                });
    }
}
