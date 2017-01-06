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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public abstract class IntervalNotifier extends AbstractNotifier {
    private final Logger logger = LoggerFactory.getLogger(IntervalNotifier.class);

    private final String cronExpression;

    private final String lastRunPath;

    public IntervalNotifier(ExtensionProperties extensionProperties, EmailMessagingService emailMessagingService, DataServicesFactory dataServicesFactory,
            ExtensionInfo extensionInfoData) {
        super(extensionProperties, emailMessagingService, dataServicesFactory, extensionInfoData);
        cronExpression = StringUtils.trimToNull(createCronExpression());
        lastRunPath = findLastRunFilePath();
    }

    private String findLastRunFilePath() {
        String path = "";
        try {
            final String configLocation = System.getProperty(ExtensionConfigManager.PROPERTY_KEY_CONFIG_LOCATION_PATH);
            final File file = new File(configLocation, getNotifierPropertyKey() + "-lastrun.txt");
            path = file.getCanonicalPath();
        } catch (final IOException ex) {
            logger.error("Cannot find last run file path", ex);
        }
        return path;
    }

    public DateRange createDateRange() {
        final Date endDate = new Date();
        Date startDate = endDate;
        try {
            final File lastRunFile = new File(lastRunPath);
            if (lastRunFile.exists()) {
                final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
                startDate = RestConnection.parseDateString(lastRunValue);
                startDate = new Date(startDate.getTime());
            } else {
                startDate = endDate;
            }
            FileUtils.write(lastRunFile, RestConnection.formatDate(endDate), "UTF-8");
        } catch (final Exception e) {
            logger.error("Error creating date range", e);
        }
        return new DateRange(startDate, endDate);
    }

    public abstract String createCronExpression();

    @Override
    public String getCronExpression() {
        return cronExpression;
    }
}
