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
package com.blackducksoftware.integration.email.model;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public class JavaMailWrapper {
    public void sendMessage(final ExtensionProperties customerProperties, final Session session, final Message message)
            throws MessagingException {
        if (customerProperties.isAuth()) {
            sendAuthenticated(customerProperties, message, session);
        } else {
            Transport.send(message);
        }
    }

    private void sendAuthenticated(final ExtensionProperties customerProperties, final Message message,
            final Session session) throws MessagingException {
        final String host = customerProperties.getHost();
        final int port = customerProperties.getPort();
        final String username = customerProperties.getUsername();
        final String password = customerProperties.getPassword();

        final Transport transport = session.getTransport("smtp");
        try {
            transport.connect(host, port, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

}
