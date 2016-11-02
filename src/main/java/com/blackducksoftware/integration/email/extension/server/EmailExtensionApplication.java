/*
 * Copyright (C) 2016 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.email.extension.server;

import org.restlet.routing.Router;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.extension.server.resources.EmailTestServerResource;

public class EmailExtensionApplication extends RestletApplication {

    public EmailExtensionApplication(TokenManager tokenManager, ExtensionConfigManager extConfigManager) {
        super(tokenManager, extConfigManager);
    }

    @Override
    public void additionalRouterConfig(final Router router) {
        super.additionalRouterConfig(router);
        router.attach(ExtensionServerConstants.EXTENSION_TEST, EmailTestServerResource.class);
    }
}
