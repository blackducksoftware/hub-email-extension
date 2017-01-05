/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.blackducksoftware.integration.email.mock;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.notifier.AbstractNotifier;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public class MockNotifier extends AbstractNotifier {
    public static final String CRON_EXPRESSION = "0 0/1 * 1/1 * ? *";

    public final static long NOTIFIER_INTERVAL = 5000;

    private final String templateName;

    private boolean ran = false;

    public MockNotifier(final ExtensionProperties customerProperties, final NotificationDataService notificationService,
            final ExtensionConfigDataService extensionConfigDataService,
            final EmailMessagingService emailMessagingService, final DataServicesFactory dataservicesFactory, final ExtensionInfo extensionInfoData,
            final String templateName) {
        super(customerProperties, emailMessagingService, dataservicesFactory, extensionInfoData);
        this.templateName = templateName;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public String getNotifierPropertyKey() {
        return templateName;
    }

    @Override
    public void run() {
        ran = true;
    }

    public boolean hasRun() {
        return ran;
    }

    @Override
    public String getCronExpression() {
        return CRON_EXPRESSION;
    }
}
