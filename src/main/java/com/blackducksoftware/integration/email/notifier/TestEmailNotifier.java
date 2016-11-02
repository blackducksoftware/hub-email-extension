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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
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

    public String emailAddress;

    public TestEmailNotifier(ExtensionProperties extensionProperties, EmailMessagingService emailMessagingService, DataServicesFactory dataServicesFactory) {
        super(extensionProperties, emailMessagingService, dataServicesFactory);
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
        return StringUtils.isNotBlank(emailAddress);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
