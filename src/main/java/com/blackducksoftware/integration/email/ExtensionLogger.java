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

}
