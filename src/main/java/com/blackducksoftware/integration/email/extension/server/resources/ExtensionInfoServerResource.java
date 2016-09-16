package com.blackducksoftware.integration.email.extension.server.resources;

import java.util.List;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import com.blackducksoftware.integration.email.extension.server.ExtensionServerConstants;
import com.blackducksoftware.integration.email.extension.server.api.model.ExtensionDescriptor;
import com.blackducksoftware.integration.email.extension.server.api.model.ResourceLink;
import com.blackducksoftware.integration.email.extension.server.api.model.ResourceMetadata;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthServerConstants;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.extension.server.oauth.resources.OAuthServerResource;
import com.google.common.collect.Lists;

public class ExtensionInfoServerResource extends OAuthServerResource {

	@Get("json")
	public ExtensionDescriptor represent() {
		// TODO define the ExtensionDescriptor when the application starts up
		// based on properties.
		final TokenManager tokenManager = getTokenManager();
		if (tokenManager != null) {
			// oauth paths
			final String configAddress = buildAddress(tokenManager, OAuthServerConstants.EXTENSION_CONFIG);
			final String clientConfigAddress = buildAddress(tokenManager, OAuthServerConstants.REGISTRATION);
			final String authAddress = buildAddress(tokenManager, OAuthServerConstants.AUTH_GRANT);
			final String callbackAddress = buildAddress(tokenManager, OAuthServerConstants.CALLBACK);

			// extension configuration paths.
			final String infoAddress = buildAddress(tokenManager, ExtensionServerConstants.EXTENSION_INFO);
			final String globalOptionsAddress = buildAddress(tokenManager,
					ExtensionServerConstants.GLOBAL_CONFIG_VALUES);
			final String userOptionsAddress = buildAddress(tokenManager, ExtensionServerConstants.USER_CONFIG_VALUES);

			final List<ResourceLink> links = Lists.newArrayList();
			links.add(new ResourceLink("extension-config", configAddress.toString()));
			links.add(new ResourceLink("client-config", clientConfigAddress.toString()));
			links.add(new ResourceLink("extension-authenticate", authAddress.toString()));
			links.add(new ResourceLink("global-config-options", globalOptionsAddress.toString()));
			links.add(new ResourceLink("user-config-options", userOptionsAddress.toString()));
			links.add(new ResourceLink("oauth-callback", callbackAddress.toString()));

			return new ExtensionDescriptor("Email Extension", "An extension to send notifications based on emails",
					"com.blackducksoftware.integration-hub-email-extension-1.0.0-SNAPSHOT",
					new ResourceMetadata(infoAddress.toString(), links));
		} else {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new ExtensionDescriptor("Email Extension", "An extension to send notifications based on emails",
					"com.blackducksoftware.integration-hub-email-extension-1.0.0-SNAPSHOT",
					new ResourceMetadata("", Lists.newArrayList()));
		}
	}

	private String buildAddress(final TokenManager tokenManager, final String path) {
		final Reference ref = new Reference(tokenManager.getLocalAddress());
		ref.setPath(path);
		return ref.toString();
	}
}
