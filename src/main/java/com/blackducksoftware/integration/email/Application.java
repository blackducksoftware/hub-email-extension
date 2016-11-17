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
package com.blackducksoftware.integration.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public final EmailEngine emailEngine;

    public static void main(final String[] args) {
        Thread shutDownThread = null;
        try {
            // Set slf4j logger facade
            System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");

            final Application app = new Application();
            shutDownThread = new Thread() {
                @Override
                public void run() {
                    if (app != null) {
                        if (app.emailEngine != null) {
                            app.emailEngine.shutDown();
                        }
                    }
                }
            };

        } catch (final Exception e) {
            logger.error("Exception occured during application execution", e);
        } finally {
            if (shutDownThread != null) {
                Runtime.getRuntime().addShutdownHook(shutDownThread);
            }
        }
    }

    public Application() throws Exception {
        emailEngine = new EmailEngine();
        emailEngine.start();
    }
}
