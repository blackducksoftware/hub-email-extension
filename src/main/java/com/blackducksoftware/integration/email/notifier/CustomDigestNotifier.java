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

public class CustomDigestNotifier extends AbstractDigestNotifier {

    public CustomDigestNotifier(ExtensionProperties extensionProperties, EmailMessagingService emailMessagingService, DataServicesFactory dataServicesFactory,
            ExtensionInfo extensionInfoData) {
        super(extensionProperties, emailMessagingService, dataServicesFactory, extensionInfoData);
    }

    @Override
    public String getCategory() {
        return "Custom Interval";
    }

    @Override
    public String createCronExpression() {
        final String cronExpression = getExtensionProperties().getNotifierVariableProperties()
                .get(getNotifierPropertyKey() + ".cron.expression");
        return cronExpression;
    }

    @Override
    public String getNotifierPropertyKey() {
        return "customDigest";
    }
}
