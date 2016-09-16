package com.blackducksoftware.integration.email.extension.oauth.server;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

import com.blackducksoftware.integration.email.extension.ExtensionConfigurationItem;
import com.blackducksoftware.integration.email.extension.oauth.TokenManager;

public class OAuthConfigurationResource extends OAuthServerResource {

	@Post
	public void configure(final JsonRepresentation request) {
		final ExtensionConfigurationItem item = new ExtensionConfigurationItem(request.getJsonObject());
		final TokenManager tokenManager = getTokenManager();

		if (tokenManager != null) {
			tokenManager.getConfiguration().setAddresses(item.getHubBaseUrl(), item.getExtensionUrl(),
					item.getoAuthAuthorizeUrl(), item.getoAuthTokenUrl());
		} else {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
