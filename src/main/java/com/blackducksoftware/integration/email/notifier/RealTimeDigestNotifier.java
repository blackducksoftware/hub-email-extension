/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.email.notifier;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;

public class RealTimeDigestNotifier extends AbstractDigestNotifier {

    public RealTimeDigestNotifier(final ExtensionProperties extensionProperties, final EmailMessagingService emailMessagingService,
            final DataServicesFactory dataServicesFactory, final ExtensionInfo extensionInfoData) {
        super(extensionProperties, emailMessagingService, dataServicesFactory, extensionInfoData);
    }

    @Override
    public String getNotifierPropertyKey() {
        return "realTimeDigest";
    }

    @Override
    public String getCategory() {
        return "Real Time";
    }

    @Override
    public String createCronExpression() {
        // every minute
        return "0 0/1 * 1/1 * ? *";
    }
}
