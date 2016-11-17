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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;

public class MockMailWrapper extends JavaMailWrapper {

    public final static String MESSAGE_EXCEPTION = "Mock thrown exception";

    private final boolean throwException;

    public MockMailWrapper(final boolean throwException) {
        this.throwException = throwException;
    }

    @Override
    public void sendMessage(final ExtensionProperties customerProperties, final Session session, final Message message)
            throws MessagingException {
        if (throwException) {
            throw new MessagingException(MESSAGE_EXCEPTION);
        } else {
            System.out.println("MockMailWrapper: sendMessage called");
        }
    }
}
