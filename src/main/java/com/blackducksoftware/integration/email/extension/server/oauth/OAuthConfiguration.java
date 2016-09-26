package com.blackducksoftware.integration.email.extension.server.oauth;

import java.util.Optional;

import org.restlet.data.Reference;
import org.restlet.ext.oauth.AccessTokenClientResource;
import org.restlet.ext.oauth.GrantType;
import org.restlet.ext.oauth.OAuthParameters;
import org.restlet.ext.oauth.ResponseType;

public class OAuthConfiguration {
	private String clientId;
	private String callbackUrl;
	private String hubUri;
	private String extensionUri;
	private String oAuthAuthorizeUri;
	private String oAuthTokenUri;
	private String userRefreshToken;

	public OAuthConfiguration() {
	}

	public boolean isClientConfigured() {
		return clientId != null;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(final String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getHubUri() {
		return hubUri;
	}

	public void setHubUri(final String hubUri) {
		this.hubUri = hubUri;
	}

	public String getExtensionUri() {
		return extensionUri;
	}

	public void setExtensionUri(final String extensionUri) {
		this.extensionUri = extensionUri;
	}

	public String getoAuthAuthorizeUri() {
		return oAuthAuthorizeUri;
	}

	public void setoAuthAuthorizeUri(final String oAuthAuthorizeUri) {
		this.oAuthAuthorizeUri = oAuthAuthorizeUri;
	}

	public String getoAuthTokenUri() {
		return oAuthTokenUri;
	}

	public void setoAuthTokenUri(final String oAuthTokenUri) {
		this.oAuthTokenUri = oAuthTokenUri;
	}

	public String getUserRefreshToken() {
		return userRefreshToken;
	}

	public void setUserRefreshToken(final String userRefreshToken) {
		this.userRefreshToken = userRefreshToken;
	}

	public void setAddresses(final String hubUri, final String extensionUri, final String oAuthAuthorizeUri,
			final String oAuthTokenUri) {
		this.hubUri = hubUri;
		this.extensionUri = extensionUri;
		this.oAuthAuthorizeUri = oAuthAuthorizeUri;
		this.oAuthTokenUri = oAuthTokenUri;
	}

	// TODO move the behavior into the oAuthconfigManager
	public Reference getOAuthAuthorizationUrl(final Optional<StateUrlProcessor> state) {
		final Reference reference = new Reference(oAuthAuthorizeUri);

		final OAuthParameters parameters = new OAuthParameters();
		parameters.responseType(ResponseType.code);
		parameters.add(OAuthParameters.CLIENT_ID, clientId);
		parameters.redirectURI(callbackUrl.toString());
		parameters.scope(new String[] { "read" });

		if (state.isPresent()) {
			final Optional<String> stateUrlValue = state.get().encode();

			if (stateUrlValue.isPresent()) {
				parameters.state(stateUrlValue.get());
			}
		}

		return parameters.toReference(reference.toString());
	}

	public AccessTokenClientResource getTokenResource() {
		final Reference reference = new Reference(oAuthTokenUri);

		final AccessTokenClientResource tokenResource = new AccessTokenClientResource(reference);
		// Client ID here and not on OAuthParams so that it can auto-add to
		// parameters internally. null auth so it does
		// NPE trying to format challenge response
		tokenResource.setClientCredentials(clientId, null);
		tokenResource.setAuthenticationMethod(null);

		return tokenResource;
	}

	public OAuthParameters getAccessTokenParameters(final String code) {
		final OAuthParameters parameters = new OAuthParameters();
		parameters.grantType(GrantType.authorization_code);
		parameters.redirectURI(callbackUrl.toString());
		parameters.code(code);

		return parameters;
	}

	public OAuthParameters getClientTokenParameters() {
		final OAuthParameters parameters = new OAuthParameters();
		parameters.grantType(GrantType.client_credentials);
		parameters.scope(new String[] { "read", "write" });

		return parameters;
	}

	public OAuthParameters getRefreshTokenParameters(final String refreshToken) {
		final OAuthParameters parameters = new OAuthParameters();
		parameters.grantType(GrantType.refresh_token);
		parameters.refreshToken(refreshToken);

		return parameters;
	}
}
