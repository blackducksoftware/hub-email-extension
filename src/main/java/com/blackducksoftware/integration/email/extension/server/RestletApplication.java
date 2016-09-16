package com.blackducksoftware.integration.email.extension.server;

import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

import com.blackducksoftware.integration.email.extension.server.oauth.AbstractOAuthApplication;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.extension.server.resources.ExtensionInfoServerResource;
import com.blackducksoftware.integration.email.extension.server.resources.GlobalConfigServerResource;
import com.blackducksoftware.integration.email.extension.server.resources.UserConfigServerResource;

public class RestletApplication extends AbstractOAuthApplication {

	public RestletApplication(final TokenManager tokenManager) {
		super(tokenManager);
	}

	@Override
	public void additionalRouterConfig(final Router router) {
		router.attach("/",
				new Redirector(getContext(), ExtensionServerConstants.EXTENSION_INFO, Redirector.MODE_CLIENT_FOUND));

		router.attach(ExtensionServerConstants.EXTENSION_INFO, ExtensionInfoServerResource.class);
		router.attach(ExtensionServerConstants.GLOBAL_CONFIG_VALUES, GlobalConfigServerResource.class);
		router.attach(ExtensionServerConstants.USER_CONFIG_VALUES, UserConfigServerResource.class);
	}
}
