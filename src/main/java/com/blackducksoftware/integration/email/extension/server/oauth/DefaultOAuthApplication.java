package com.blackducksoftware.integration.email.extension.server.oauth;

import org.restlet.routing.Router;

public class DefaultOAuthApplication extends AbstractOAuthApplication {

	public DefaultOAuthApplication(final TokenManager tokenManager) {
		super(tokenManager);
	}

	@Override
	public void additionalRouterConfig(final Router router) {
		// no additional configuration
	}
}
