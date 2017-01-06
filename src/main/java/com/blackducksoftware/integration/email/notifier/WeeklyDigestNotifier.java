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
package com.blackducksoftware.integration.email.notifier;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public class WeeklyDigestNotifier extends AbstractDigestNotifier {

    public WeeklyDigestNotifier(final ExtensionProperties customerProperties,
            final NotificationDataService notificationDataService,
            final ExtensionConfigDataService extensionConfigDataService,
            final EmailMessagingService emailMessagingService, final DataServicesFactory dataservicesFactory, final ExtensionInfo extensionInfoData) {
        super(customerProperties, emailMessagingService, dataservicesFactory, extensionInfoData);
    }

    @Override
    public DateRange createDateRange() {
        final ZonedDateTime currentTime = ZonedDateTime.now();
        final ZoneId zone = currentTime.getZone();
        final ZonedDateTime endZonedTime = ZonedDateTime.of(currentTime.getYear(), currentTime.getMonthValue(),
                currentTime.getDayOfMonth(), 23, 59, 59, 999, zone).minusDays(1);

        final ZonedDateTime startZonedTime = ZonedDateTime
                .of(currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(), 0, 0, 0, 0, zone)
                .minusDays(8);

        return new DateRange(Date.from(startZonedTime.toInstant()), Date.from(endZonedTime.toInstant()));
    }

    @Override
    public String getNotifierPropertyKey() {
        return "weeklyDigest";
    }

    @Override
    public String getCategory() {
        return "Weekly";
    }

    @Override
    public String createCronExpression() {
        // every Sunday 6am
        return "0 0 06 * * SUN *";
    }
}
