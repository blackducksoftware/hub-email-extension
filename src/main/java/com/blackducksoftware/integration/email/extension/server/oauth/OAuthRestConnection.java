package com.blackducksoftware.integration.email.extension.server.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.Context;
import org.restlet.resource.ClientResource;

import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class OAuthRestConnection extends RestConnection {

	private final TokenManager tokenManager;

	public OAuthRestConnection(final HubServerConfig hubServerConfig, final TokenManager tokenManager) {
		setBaseUrl(hubServerConfig.getHubUrl().toString());
		setProxyProperties(hubServerConfig.getProxyInfo());
		setTimeout(hubServerConfig.getTimeout());
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
		} catch (final IOException ex) {
			return new ClientResource(context, new URI(providedUrl));
		}
	}
}
