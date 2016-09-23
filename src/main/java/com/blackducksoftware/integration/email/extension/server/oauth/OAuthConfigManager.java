package com.blackducksoftware.integration.email.extension.server.oauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthConfigManager {

	public static final String OAUTH_CONFIG_FILE_NAME = "oauth.properties";
	public static final String OAUTH_PROPERTY_CLIENT_ID = "client.id";
	public static final String OAUTH_PROPERTY_USER_REFRESH_TOKEN = "user.refresh.token";
	public static final String OAUTH_PROPERTY_CALLBACK_URL = "callback.url";
	public static final String OAUTH_PROPERTY_HUB_URI = "hub.uri";
	public static final String OAUTH_PROPERTY_EXTENSION_URI = "hub.extension.uri";
	public static final String OAUTH_PROPERTY_AUTHORIZE_URI = "hub.authorize.uri";
	public static final String OAUTH_PROPERTY_TOKEN_URI = "hub.token.uri";

	private static final String MSG_COULD_NOT_LOAD_PROPS = "Could not load properties file.  OAUTH client will need to be Authorized";
	private static final String MSG_PROPERTY_FILE_LOCATION = "Property file location: {}";

	public final Logger logger = LoggerFactory.getLogger(OAuthConfigManager.class);

	public OAuthConfiguration load() {
		final File propFile = getPropFile();
		logger.info("Loading OAUTH configuration...");
		try {
			logger.info(MSG_PROPERTY_FILE_LOCATION, propFile.getCanonicalPath());
		} catch (final IOException e) {
			// ignore
		}

		if (propFile.exists()) {
			final Properties props = new Properties();
			try (FileInputStream fileInputStream = new FileInputStream(propFile)) {
				props.load(fileInputStream);
			} catch (final IOException e) {
				logger.error(MSG_COULD_NOT_LOAD_PROPS, e);
				return new OAuthConfiguration();
			}
			return createFromProperties(props);
		} else {
			logger.error(MSG_COULD_NOT_LOAD_PROPS);
			return new OAuthConfiguration();
		}
	}

	public void persist(final OAuthConfiguration config) {
		final File propFile = getPropFile();
		logger.info("Saving OAuth configuration...");
		try {
			logger.info(MSG_PROPERTY_FILE_LOCATION, propFile.getCanonicalFile());
		} catch (final IOException e) {
			// ignore
		}
		if (propFile != null && !propFile.exists()) {
			final File parent = propFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
		}

		try (FileOutputStream outputStream = new FileOutputStream(propFile)) {
			final Properties props = new Properties();
			props.put(OAUTH_PROPERTY_CLIENT_ID, StringUtils.trimToEmpty(config.getClientId()));
			props.put(OAUTH_PROPERTY_USER_REFRESH_TOKEN, StringUtils.trimToEmpty(config.getUserRefreshToken()));
			props.put(OAUTH_PROPERTY_CALLBACK_URL, StringUtils.trimToEmpty(config.getCallbackUrl()));
			props.put(OAUTH_PROPERTY_HUB_URI, StringUtils.trimToEmpty(config.getHubUri()));
			props.put(OAUTH_PROPERTY_EXTENSION_URI, StringUtils.trimToEmpty(config.getExtensionUri()));
			props.put(OAUTH_PROPERTY_AUTHORIZE_URI, StringUtils.trimToEmpty(config.getoAuthAuthorizeUri()));
			props.put(OAUTH_PROPERTY_TOKEN_URI, StringUtils.trimToEmpty(config.getoAuthTokenUri()));
			props.store(outputStream, "OAUTH Client configuration");
		} catch (final IOException e) {
			logger.error("Could not save OAUTH configuration", e);
		}
	}

	private File getPropFile() {
		final String parentLocation = System.getProperty("ext.config.location");

		if (StringUtils.isNotBlank(parentLocation)) {
			return new File(parentLocation, OAUTH_CONFIG_FILE_NAME);
		} else {
			return new File(OAUTH_CONFIG_FILE_NAME);
		}
	}

	private OAuthConfiguration createFromProperties(final Properties properties) {
		final String clientId = properties.getProperty(OAUTH_PROPERTY_CLIENT_ID);
		final String userRefreshToken = properties.getProperty(OAUTH_PROPERTY_USER_REFRESH_TOKEN);
		final String callbackUrl = properties.getProperty(OAUTH_PROPERTY_CALLBACK_URL);
		final String hubUri = properties.getProperty(OAUTH_PROPERTY_HUB_URI);
		final String extensionUri = properties.getProperty(OAUTH_PROPERTY_EXTENSION_URI);
		final String authorizeUri = properties.getProperty(OAUTH_PROPERTY_AUTHORIZE_URI);
		final String tokenUri = properties.getProperty(OAUTH_PROPERTY_TOKEN_URI);
		final OAuthConfiguration config = new OAuthConfiguration();
		config.setClientId(clientId);
		config.setCallbackUrl(callbackUrl);
		config.setUserRefreshToken(userRefreshToken);
		config.setAddresses(hubUri, extensionUri, authorizeUri, tokenUri);

		return config;
	}
}
