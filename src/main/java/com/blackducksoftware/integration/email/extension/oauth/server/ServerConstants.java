package com.blackducksoftware.integration.email.extension.oauth.server;

public class ServerConstants {

	public static final String REGISTRATION = "/configuration";
	public static final String CALLBACK = "/callback";
	public static final String AUTH_GRANT = "/extension/auth";

	private ServerConstants() throws InstantiationException {
		throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
	}

}
