package com.blackducksoftware.integration.email.extension.server.oauth;

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
}
