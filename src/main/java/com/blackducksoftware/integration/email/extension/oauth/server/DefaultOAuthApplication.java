package com.blackducksoftware.integration.email.extension.oauth.server;

import org.restlet.routing.Router;

import com.blackducksoftware.integration.email.extension.oauth.TokenManager;

public class DefaultOAuthApplication extends AbstractOAuthApplication {

	public DefaultOAuthApplication(final TokenManager tokenManager) {
		super(tokenManager);
	}

	@Override
	public void additionalRouterConfig(final Router router) {
		// no additional configuration
	}
}
