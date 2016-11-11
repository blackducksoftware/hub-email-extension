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
package com.blackducksoftware.integration.email.extension.server.resources;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.blackducksoftware.integration.email.EmailExtensionConstants;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.notifier.TestEmailNotifier;

public class EmailTestServerResource extends ExtensionServerResource {

    private static final String TEST_FORM_HTML = "testform.html";

    private static final String FORM_INPUT_EMAIL_ADDRESS = "emailAddress";

    @Post
    public void postFormData(final Representation entity) {
        TokenManager tokenManager = getTokenManager();
        if (tokenManager.authenticationRequired()) {
            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        } else {
            Form form = new Form(entity);
            final String emailAddress = form.getFirstValue(FORM_INPUT_EMAIL_ADDRESS);
            if (StringUtils.isNotBlank(emailAddress)) {
                TestEmailNotifier testNotifier = (TestEmailNotifier) getContext().getAttributes()
                        .get(EmailExtensionConstants.CONTEXT_ATTRIBUTE_KEY_TEST_NOTIFIER);
                testNotifier.setEmailAddress(emailAddress);
                testNotifier.run();
                testNotifier.setEmailAddress("");
            }
        }
    }

    @Get
    public FileRepresentation getTestForm() {
        TokenManager tokenManager = this.getTokenManager();
        if (tokenManager.authenticationRequired()) {
            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        } else {
            try {
                File webDir = getWebDirectory();
                File file = new File(webDir, TEST_FORM_HTML);
                FileRepresentation htmlForm = new FileRepresentation(file, MediaType.TEXT_HTML);
                return htmlForm;
            } catch (Exception ex) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
                return null;
            }
        }
    }

    private File getWebDirectory() {
        File webDir = null;
        final String appHomeDir = System.getProperty(EmailExtensionConstants.SYSTEM_PROPERTY_KEY_APP_HOME);
        if (StringUtils.isNotBlank(appHomeDir)) {
            webDir = new File(appHomeDir, "web");
        }

        return webDir;
    }
}
