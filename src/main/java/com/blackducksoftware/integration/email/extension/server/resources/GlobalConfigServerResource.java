package com.blackducksoftware.integration.email.extension.server.resources;

import org.restlet.data.Status;
import org.restlet.resource.Get;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;

public class GlobalConfigServerResource extends ExtensionServerResource {

    @Get("json")
    public String represent() {
        final ExtensionConfigManager extConfigManager = getExtensionConfigManager();
        if (extConfigManager != null) {
            final String jsonConfig = extConfigManager.loadGlobalConfigJSON();
            return jsonConfig;
        } else {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            return "";
        }
    }
}
