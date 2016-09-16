package com.blackducksoftware.integration.email.extension.server.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.restlet.ext.oauth.AccessTokenClientResource;
import org.restlet.ext.oauth.OAuthException;
import org.restlet.ext.oauth.internal.Token;

public class TokenManager {

	public final static String CONTEXT_ATTRIBUTE_KEY = "blackduck-oauth-token-manager";

	private final OAuthConfiguration configuration;
	// Internal storage for tokens - done in-memory as a simple example
	private Token userToken = null;
	private Token clientToken = null;
	private String localAddress;

	public TokenManager() {
		configuration = new OAuthConfiguration();
	}

	public OAuthConfiguration getConfiguration() {
		return configuration;
	}

	public boolean authenticationRequired() {
		return userToken == null;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(final String localAddress) {
		this.localAddress = localAddress;
	}

	public void updateClientId(final String clientId) {
		configuration.setClientId(clientId);
	}

	public void exchangeForToken(final String authorizationCode) throws IOException {
		final AccessTokenClientResource tokenResource = configuration.getTokenResource();
		try {
			userToken = tokenResource.requestToken(configuration.getAccessTokenParameters(authorizationCode));
		} catch (JSONException | OAuthException e) {
			throw new IOException(e);
		}
	}

	public void refreshToken(final AccessType accessType) throws IOException {
		if (AccessType.USER.equals(accessType)) {
			refreshUserAccessToken();
		} else if (AccessType.CLIENT.equals(accessType)) {
			refreshClientAccessToken();
		}
	}

	public TokenClientResource createClientResource(final String reference, final AccessType accessType)
			throws IOException, URISyntaxException {
		final Token token = getToken(accessType);
		return new TokenClientResource(new URI(reference), token);
	}

	private Token getToken(final AccessType accessType) throws IOException {
		Token result = null;

		if (AccessType.USER.equals(accessType)) {
			if (userToken == null) {
				throw new IllegalStateException("User token not populated");
			} else {
				result = userToken;
			}
		} else if (AccessType.CLIENT.equals(accessType)) {
			if (clientToken == null) {
				refreshClientAccessToken();
			}
			result = clientToken;
		}

		return result;
	}

	private void refreshUserAccessToken() throws IOException {
		if (userToken != null) {
			final AccessTokenClientResource tokenResource = configuration.getTokenResource();
			try {
				userToken = tokenResource
						.requestToken(configuration.getRefreshTokenParameters(userToken.getRefreshToken()));
			} catch (JSONException | OAuthException e) {
				throw new IOException(e);
			}
		} else {
			throw new IllegalStateException("No token present to refresh");
		}
	}

	private void refreshClientAccessToken() throws IOException {
		final AccessTokenClientResource tokenResource = configuration.getTokenResource();
		try {
			clientToken = tokenResource.requestToken(configuration.getClientTokenParameters());
		} catch (JSONException | OAuthException e) {
			throw new IOException(e);
		}
	}
}
