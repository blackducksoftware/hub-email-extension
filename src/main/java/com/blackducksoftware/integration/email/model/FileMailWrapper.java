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

import java.io.File;
import java.io.FileOutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

// this mail wrapper is for testing purposes and can be swapped in the EmailEngine with the base class.
public class FileMailWrapper extends JavaMailWrapper {

    private int index = 0;

    @Override
    public void sendMessage(final ExtensionProperties customerProperties, final Session session, final Message message)
            throws MessagingException {
        final File parent = new File(customerProperties.getEmailTemplateDirectory());
        final File messagesDir = new File(parent.getParentFile(), "Test_Messages");
        messagesDir.mkdirs();
        final File file = new File(messagesDir, createFileName());
        try (FileOutputStream fileOutput = new FileOutputStream(file)) {
            file.createNewFile();
            message.writeTo(fileOutput);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private String createFileName() {
        return "Test_Message_" + ++index;
    }
}
