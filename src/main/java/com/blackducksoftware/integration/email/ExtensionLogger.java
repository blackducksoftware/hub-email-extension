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

import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;

public class ExtensionLogger extends IntLogger {
    private final Logger logger;

    public ExtensionLogger(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final String txt) {
        logger.info(txt);
    }

    @Override
    public void error(final Throwable t) {
        logger.error("", t);
    }

    @Override
    public void error(final String txt, final Throwable t) {
        logger.error(txt, t);
    }

    @Override
    public void error(final String txt) {
        logger.error(txt);
    }

    @Override
    public void warn(final String txt) {
        logger.warn(txt);
    }

    @Override
    public void trace(final String txt) {
        logger.trace(txt);
    }

    @Override
    public void trace(final String txt, final Throwable t) {
        logger.trace(txt, t);
    }

    @Override
    public void debug(final String txt) {
        logger.debug(txt);
    }

    @Override
    public void debug(final String txt, final Throwable t) {
        logger.debug(txt, t);
    }

    @Override
    public void setLogLevel(final LogLevel logLevel) {
        // cannot change the log level in slf4j
    }

    @Override
    public LogLevel getLogLevel() {
        if (logger.isDebugEnabled()) {
            return LogLevel.DEBUG;
        } else if (logger.isTraceEnabled()) {
            return LogLevel.TRACE;
        } else if (logger.isInfoEnabled()) {
            return LogLevel.INFO;
        } else if (logger.isErrorEnabled()) {
            return LogLevel.ERROR;
        } else if (logger.isWarnEnabled()) {
            return LogLevel.WARN;
        } else {
            return LogLevel.OFF;
        }
    }

    @Override
    public void alwaysLog(String txt) {
        info(txt);
    }
}
