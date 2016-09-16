package com.blackducksoftware.integration.email.extension.server;

public class ExtensionServerConstants {
	public static final String EXTENSION_INFO = "/extension/info";
	public static final String GLOBAL_CONFIG_VALUES = "/extension/values/global";
	public static final String USER_CONFIG_VALUES = "/extension/values/user";

	private ExtensionServerConstants() throws InstantiationException {
		throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
	}
}
