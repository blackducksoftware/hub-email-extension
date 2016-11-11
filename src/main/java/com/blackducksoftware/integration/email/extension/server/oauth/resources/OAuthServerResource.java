package com.blackducksoftware.integration.email.extension.server.oauth.resources;

import org.restlet.resource.ServerResource;

import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;

public class OAuthServerResource extends ServerResource {

    public TokenManager getTokenManager() {
        return (TokenManager) getContext().getAttributes().get(TokenManager.CONTEXT_ATTRIBUTE_KEY);
    }
}
