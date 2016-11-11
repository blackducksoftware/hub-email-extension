package com.blackducksoftware.integration.email.extension.server.resources;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.extension.server.oauth.resources.OAuthServerResource;

public class ExtensionServerResource extends OAuthServerResource {

    public ExtensionConfigManager getExtensionConfigManager() {
        return (ExtensionConfigManager) getContext().getAttributes().get(ExtensionConfigManager.CONTEXT_ATTRIBUTE_KEY);
    }
}
