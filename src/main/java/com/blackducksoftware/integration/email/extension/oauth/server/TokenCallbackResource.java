package com.blackducksoftware.integration.email.extension.oauth.server;

import java.io.IOException;
import java.net.URISyntaxException;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.blackducksoftware.integration.email.extension.oauth.AccessType;
import com.blackducksoftware.integration.email.extension.oauth.AuthorizationState;
import com.blackducksoftware.integration.email.extension.oauth.TokenClientResource;
import com.blackducksoftware.integration.email.extension.oauth.TokenManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class TokenCallbackResource extends OAuthServerResource {

	@Get
	public void accept() {
		final TokenManager tokenManager = getTokenManager();
		if (tokenManager != null) {
			final String authorizationCode = getQuery().getFirstValue("code");
			final String urlState = getQuery().getFirstValue("state");

			final AuthorizationState state = new AuthorizationState(urlState);
			final Reference redirectTo;

			if (state.getReturnUrl().isPresent()) {
				redirectTo = new Reference(state.getReturnUrl().get());
			} else {
				redirectTo = new Reference(tokenManager.getLocalAddress());
			}

			try {
				tokenManager.exchangeForToken(authorizationCode);

				// Update authorization status
				// this is hub specific as far as I can tell to send status for
				// the authorization.
				final String ackUrl = tokenManager.getConfiguration().getExtensionUri();
				final TokenClientResource resource = tokenManager.createClientResource(ackUrl, AccessType.CLIENT);
				try {
					updateAuthorized(resource);
				} catch (final ResourceException e) {
					if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(e.getStatus())) {
						// Try one more time, after refreshing tokens
						tokenManager.refreshToken(AccessType.CLIENT);
						updateAuthorized(resource);
					} else {
						throw e;
					}
				}
				getResponse().redirectSeeOther(redirectTo);
			} catch (final IOException | URISyntaxException e) {
				getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
			}
		} else {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, "No token manager available");
		}
	}

	private void updateAuthorized(final ClientResource resource) throws IOException {
		final Representation rep = resource.get();
		final JsonParser parser = new JsonParser();
		try {
			final JsonElement json = parser.parse(rep.getText());
			json.getAsJsonObject().add("authenticated", new JsonPrimitive(true));

			resource.put(new JsonRepresentation(json.toString()));
		} catch (final IOException e) {
			throw e;
		}
	}
}
