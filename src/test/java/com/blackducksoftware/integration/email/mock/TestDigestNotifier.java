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

import java.io.File;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.notifier.AbstractDigestNotifier;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;

public class TestDigestNotifier extends AbstractDigestNotifier {
    private final Logger logger = LoggerFactory.getLogger(TestDigestNotifier.class);

    private final String lastRunPath;

    private final String initialStartDate;

    public TestDigestNotifier(final ExtensionProperties extensionProperties,
            final EmailMessagingService emailMessagingService, HubServicesFactory hubServicesFactory) {
        super(extensionProperties, emailMessagingService, hubServicesFactory);
        lastRunPath = getExtensionProperties().getNotifierVariableProperties()
                .get(getNotifierPropertyKey() + ".lastrun.file");
        initialStartDate = getExtensionProperties().getNotifierVariableProperties()
                .get(getNotifierPropertyKey() + ".start.date");
    }

    @Override
    public DateRange createDateRange(final ZoneId zoneId) {
        try {
            Date startDate = null;
            final File lastRunFile = new File(lastRunPath);
            if (lastRunFile.exists()) {
                final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
                startDate = RestConnection.parseDateString(lastRunValue);
                startDate = new Date(startDate.getTime() + 1);
            } else {
                final String lastRunValue = initialStartDate;
                startDate = RestConnection.parseDateString(lastRunValue);
            }
            final Date endDate = new Date();
            FileUtils.write(lastRunFile, RestConnection.formatDate(endDate), "UTF-8");
            return new DateRange(startDate, endDate);
        } catch (final Exception e) {
            logger.error("Error creating date range", e);
            final Date date = new Date();
            return new DateRange(date, date);
        }
    }

    @Override
    public String getNotifierPropertyKey() {
        return "digest";
    }

    @Override
    public String getCategory() {
        return "daily";
    }
}
