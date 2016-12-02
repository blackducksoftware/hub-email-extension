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

import java.util.EnumSet;

import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.LogLevel;

public class MockLogger extends IntLogger {

    private LogLevel logLevel;

    private final EnumSet<LogLevel> infoSet = EnumSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);

    private final EnumSet<LogLevel> errorSet = EnumSet.of(LogLevel.ERROR);

    private final EnumSet<LogLevel> warnSet = EnumSet.of(LogLevel.ERROR, LogLevel.WARN);

    private final EnumSet<LogLevel> debugSet = EnumSet.allOf(LogLevel.class);

    @Override
    public void info(final String txt) {
        if (infoSet.contains(getLogLevel())) {
            System.out.println(txt);
        }
    }

    @Override
    public void error(final Throwable t) {
        if (errorSet.contains(getLogLevel())) {
            t.printStackTrace();
        }
    }

    @Override
    public void error(final String txt, final Throwable t) {
        if (errorSet.contains(getLogLevel())) {
            System.out.println(txt);
            t.printStackTrace();
        }
    }

    @Override
    public void error(final String txt) {
        if (errorSet.contains(getLogLevel())) {
            System.out.println(txt);
        }
    }

    @Override
    public void warn(final String txt) {
        if (warnSet.contains(getLogLevel())) {
            System.out.println(txt);
        }
    }

    @Override
    public void trace(final String txt) {
        if (debugSet.contains(getLogLevel())) {
            System.out.println(txt);
        }
    }

    @Override
    public void trace(final String txt, final Throwable t) {
        if (debugSet.contains(getLogLevel())) {
            System.out.println(txt);
            t.printStackTrace();
        }
    }

    @Override
    public void debug(final String txt) {
        if (debugSet.contains(getLogLevel())) {
            System.out.println(txt);
        }
    }

    @Override
    public void debug(final String txt, final Throwable t) {
        if (debugSet.contains(getLogLevel())) {
            System.out.println(txt);
        }
    }

    @Override
    public void setLogLevel(final LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public LogLevel getLogLevel() {
        return logLevel;
    }

    @Override
    public void alwaysLog(String txt) {
        System.out.println(txt);
    }
}
