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
package com.blackducksoftware.integration.email.notifier;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityRequestService;
import com.blackducksoftware.integration.hub.dataservice.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservice.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubRequestService;

public class RealTimeNotifier extends AbstractDigestNotifier {
    private final Logger logger = LoggerFactory.getLogger(RealTimeNotifier.class);

    private final String lastRunPath;

    public RealTimeNotifier(ExtensionProperties extensionProperties, EmailMessagingService emailMessagingService, HubRequestService hubRequestService,
            VulnerabilityRequestService vulnerabilityRequestService, ExtensionConfigDataService extensionConfigDataService,
            NotificationDataService notificationDataService) {
        super(extensionProperties, emailMessagingService, hubRequestService, vulnerabilityRequestService, extensionConfigDataService, notificationDataService);
        lastRunPath = getExtensionProperties().getNotifierVariableProperties()
                .get(getNotifierPropertyKey() + ".lastrun.file");
    }

    @Override
    public DateRange createDateRange(ZoneId zone) {
        final LocalDateTime currentTime = LocalDateTime.now();
        final ZonedDateTime endZonedTime = ZonedDateTime.of(currentTime, zone);
        ZonedDateTime startZonedTime;
        final Date endDate = Date.from(endZonedTime.toInstant());
        Date startDate = endDate;
        try {
            final File lastRunFile = new File(lastRunPath);
            if (lastRunFile.exists()) {
                final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
                startDate = RestConnection.parseDateString(lastRunValue);
                startDate = new Date(startDate.getTime() + 1);
                startZonedTime = ZonedDateTime.ofInstant(startDate.toInstant(), zone);
                startDate = Date.from(startZonedTime.toInstant());
            } else {
                startDate = endDate;
            }
            FileUtils.write(lastRunFile, RestConnection.formatDate(endDate), "UTF-8");
        } catch (final Exception e) {
            logger.error("Error creating date range", e);
        }
        return new DateRange(startDate, endDate);
    }

    @Override
    public String getNotifierPropertyKey() {
        return "realTimeDigest";
    }

    @Override
    public String getCategory() {
        return "Real Time";
    }
}
