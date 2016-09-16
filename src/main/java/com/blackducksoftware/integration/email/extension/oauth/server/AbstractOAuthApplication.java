package com.blackducksoftware.integration.email.extension.oauth.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.blackducksoftware.integration.email.extension.oauth.TokenManager;

public abstract class AbstractOAuthApplication extends Application {

	private final TokenManager tokenManager;

	public AbstractOAuthApplication(final TokenManager tokenManager) {
		super();
		this.tokenManager = tokenManager;
	}

	@Override
	public Restlet createInboundRoot() {
		getContext().getAttributes().put(TokenManager.CONTEXT_ATTRIBUTE_KEY, tokenManager);
		final Router router = new Router(getContext());

		router.attach(OAuthServerConstants.REGISTRATION, ClientIdRegistrationResource.class);
		router.attach(OAuthServerConstants.EXTENSION_CONFIG, OAuthConfigurationResource.class);
		router.attach(OAuthServerConstants.CALLBACK, TokenCallbackResource.class);
		router.attach(OAuthServerConstants.AUTH_GRANT, TokenAuthenticationResource.class);
		additionalRouterConfig(router);
		return router;
	}

	public abstract void additionalRouterConfig(final Router router);
}
