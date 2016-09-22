package com.blackducksoftware.integration.email.extension.server.oauth;

public class OAuthServerConstants {

	public static final String REGISTRATION = "/configuration";
	public static final String CALLBACK = "/callback";
	public static final String EXTENSION_CONFIG = "/extension/config";
	public static final String AUTH_GRANT = "/extension/auth";

	private OAuthServerConstants() throws InstantiationException {
		throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
	}
}
