package com.blackducksoftware.integration.email.extension.server.oauth.resources;

import java.io.IOException;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import com.blackducksoftware.integration.email.extension.server.oauth.StateUrlProcessor;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;

public class TokenCallbackResource extends OAuthServerResource {

    @Get
    public void accept() {
        final TokenManager tokenManager = getTokenManager();
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
            } catch (final IOException e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            }
        } else {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, "No token manager available");
        }
    }
}
