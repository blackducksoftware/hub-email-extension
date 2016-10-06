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
}
