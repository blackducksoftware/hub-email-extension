package com.blackducksoftware.integration.email.extension.server.oauth;

import java.io.IOException;
import java.net.URISyntaxException;

import org.restlet.Context;
import org.restlet.resource.ClientResource;

import com.blackducksoftware.integration.hub.rest.RestConnection;

public class OAuthRestConnection extends RestConnection {

	private final TokenManager tokenManager;

	public OAuthRestConnection(final String baseUrl, final TokenManager tokenManager) {
		super(baseUrl);
		this.tokenManager = tokenManager;
	}

	public TokenManager getTokenManager() {
		return tokenManager;
	}

	@Override
	public ClientResource createClientResource(final Context context, final String providedUrl)
			throws URISyntaxException {
		try {
			return tokenManager.createClientResource(providedUrl, AccessType.USER);
		} catch (final IOException e) {
			return super.createClientResource(context, providedUrl);
		}
	}
}
