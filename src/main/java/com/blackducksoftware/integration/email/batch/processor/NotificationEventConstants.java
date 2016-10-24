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
package com.blackducksoftware.integration.email.batch.processor;

public class NotificationEventConstants {

    public static final String EVENT_KEY_NAME_VALUE_SEPARATOR = "=";

    public static final String EVENT_KEY_NAME_VALUE_PAIR_SEPARATOR = "|";

    public static final String EVENT_KEY_ISSUE_TYPE_NAME = "t";

    public static final String EVENT_KEY_ISSUE_TYPE_VALUE_POLICY = "p";

    public static final String EVENT_KEY_ISSUE_TYPE_VALUE_VULNERABILITY = "v";

    public static final String EVENT_KEY_JIRA_PROJECT_ID_NAME = "jp";

    public static final String EVENT_KEY_HUB_PROJECT_VERSION_REL_URL_HASHED_NAME = "hpv";

    public static final String EVENT_KEY_HUB_COMPONENT_REL_URL_HASHED_NAME = "hc";

    public static final String EVENT_KEY_HUB_COMPONENT_VERSION_REL_URL_HASHED_NAME = "hcv";

    public static final String EVENT_KEY_HUB_POLICY_RULE_REL_URL_HASHED_NAME = "hr";

    private NotificationEventConstants() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }
}
