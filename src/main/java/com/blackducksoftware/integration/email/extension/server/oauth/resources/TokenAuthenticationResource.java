package com.blackducksoftware.integration.email.extension.server.oauth.resources;

import java.util.Optional;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.server.oauth.AuthorizationState;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;

public class TokenAuthenticationResource extends OAuthServerResource {

	private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationResource.class);

	@Get
	public void authenticate() {
		// Use state if provided
		final String next = getRequest().getResourceRef().getQueryAsForm(true).getFirstValue("next");
		final AuthorizationState state = new AuthorizationState(getQueryValue("state"));

		if (state.getReturnUrl().isPresent() && next != null) {
			state.setReturnUrl(next);
		} else if (getRequest().getReferrerRef() != null) {
			state.setReturnUrl(getRequest().getReferrerRef().toString());
		}
		final TokenManager tokenManager = getTokenManager();
		if (tokenManager != null) {
			logger.info("Authenticate method called to obtain authorization url");
			final Reference authUrl = tokenManager.getConfiguration().getOAuthAuthorizationUrl(Optional.of(state));
			getResponse().redirectSeeOther(authUrl);
		} else {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
