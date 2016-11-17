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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;

import freemarker.template.TemplateException;

public class TestEmailNotifier extends AbstractNotifier {
    private final Logger logger = LoggerFactory.getLogger(TestEmailNotifier.class);

    private String emailAddress;

    private final Pattern emailPattern;

    public TestEmailNotifier(final ExtensionProperties extensionProperties, final EmailMessagingService emailMessagingService,
            final DataServicesFactory dataServicesFactory) {
        super(extensionProperties, emailMessagingService, dataServicesFactory);
        emailPattern = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$");
    }

    @Override
    public String getTemplateName() {
        return "sampleTemplate.ftl";
    }

    @Override
    public String getCronExpression() {
        return "";
    }

    @Override
    public String getNotifierPropertyKey() {
        return "testEmailNotifier";
    }

    @Override
    public void run() {
        try {
            if (isEmailAddressValid(emailAddress) == false) {
                logger.info("Test email address {} is invalid.", emailAddress);
            } else {
                ExtensionProperties globalConfig = createPropertiesFromGlobalConfig();
                final Map<String, Object> model = new HashMap<>();
                final EmailTarget emailTarget = new EmailTarget(emailAddress, getTemplateName(), model);
                getEmailMessagingService().sendEmailMessage(emailTarget, globalConfig);
            }
        } catch (UnexpectedHubResponseException | MessagingException | IOException | TemplateException ex) {
            logger.error("Error occurred sending test email.", ex);
        }
    }

    private boolean isEmailAddressValid(String emailAddress) {
        Matcher matcher = emailPattern.matcher(emailAddress);
        return matcher.matches();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
