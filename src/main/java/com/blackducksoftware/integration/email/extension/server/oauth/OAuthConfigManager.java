package com.blackducksoftware.integration.email.extension.server.oauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Reference;
import org.restlet.ext.oauth.AccessTokenClientResource;
import org.restlet.ext.oauth.GrantType;
import org.restlet.ext.oauth.OAuthParameters;
import org.restlet.ext.oauth.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.exception.EncryptionException;

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
        } catch (final IOException | IllegalArgumentException | EncryptionException e) {
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
            props.put(OAUTH_PROPERTY_CLIENT_ID, encodePropertyValue(StringUtils.trimToEmpty(config.getClientId())));
            props.put(OAUTH_PROPERTY_USER_REFRESH_TOKEN, encodePropertyValue(StringUtils.trimToEmpty(config.getUserRefreshToken())));
            props.put(OAUTH_PROPERTY_CALLBACK_URL, encodePropertyValue(StringUtils.trimToEmpty(config.getCallbackUrl())));
            props.put(OAUTH_PROPERTY_HUB_URI, encodePropertyValue(StringUtils.trimToEmpty(config.getHubUri())));
            props.put(OAUTH_PROPERTY_EXTENSION_URI, encodePropertyValue(StringUtils.trimToEmpty(config.getExtensionUri())));
            props.put(OAUTH_PROPERTY_AUTHORIZE_URI, encodePropertyValue(StringUtils.trimToEmpty(config.getoAuthAuthorizeUri())));
            props.put(OAUTH_PROPERTY_TOKEN_URI, encodePropertyValue(StringUtils.trimToEmpty(config.getoAuthTokenUri())));
            props.store(outputStream, "OAUTH Client configuration");
        } catch (final IOException | IllegalArgumentException | EncryptionException e) {
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

    private OAuthConfiguration createFromProperties(final Properties properties) throws IllegalArgumentException, EncryptionException {
        final String clientId = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_CLIENT_ID));
        final String userRefreshToken = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_USER_REFRESH_TOKEN));
        final String callbackUrl = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_CALLBACK_URL));
        final String hubUri = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_HUB_URI));
        final String extensionUri = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_EXTENSION_URI));
        final String authorizeUri = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_AUTHORIZE_URI));
        final String tokenUri = getPropertyValue(properties.getProperty(OAUTH_PROPERTY_TOKEN_URI));
        final OAuthConfiguration config = new OAuthConfiguration();
        config.setClientId(clientId);
        config.setCallbackUrl(callbackUrl);
        config.setUserRefreshToken(userRefreshToken);
        config.setAddresses(hubUri, extensionUri, authorizeUri, tokenUri);

        return config;
    }

    private String getPropertyValue(String propertyValue) throws IllegalArgumentException, EncryptionException {
        final Decoder decoder = Base64.getUrlDecoder();
        final String value = new String(decoder.decode(propertyValue));
        return value;
    }

    private String encodePropertyValue(String value) throws IllegalArgumentException, EncryptionException {
        // simply obfuscate the values from clear text.
        Encoder encoder = Base64.getUrlEncoder();
        String encoded = encoder.encodeToString(value.getBytes());
        return encoded;
    }

    public String getOAuthAuthorizationUrl(final OAuthConfiguration config, final Optional<StateUrlProcessor> state) {
        final Reference reference = new Reference(config.getoAuthAuthorizeUri());

        final OAuthParameters parameters = new OAuthParameters();
        parameters.responseType(ResponseType.code);
        parameters.add(OAuthParameters.CLIENT_ID, config.getClientId());
        parameters.redirectURI(config.getCallbackUrl());
        parameters.scope(new String[] { "read" });

        if (state.isPresent()) {
            final Optional<String> stateUrlValue = state.get().encode();

            if (stateUrlValue.isPresent()) {
                parameters.state(stateUrlValue.get());
            }
        }

        return parameters.toReference(reference.toString()).toString();
    }

    public AccessTokenClientResource getTokenResource(final OAuthConfiguration config) {
        final Reference reference = new Reference(config.getoAuthTokenUri());

        final AccessTokenClientResource tokenResource = new AccessTokenClientResource(reference);
        // Client ID here and not on OAuthParams so that it can auto-add to
        // parameters internally. null auth so it does
        // NPE trying to format challenge response
        tokenResource.setClientCredentials(config.getClientId(), null);
        tokenResource.setAuthenticationMethod(null);

        return tokenResource;
    }

    public OAuthParameters getAccessTokenParameters(final OAuthConfiguration config, final String code) {
        final OAuthParameters parameters = new OAuthParameters();
        parameters.grantType(GrantType.authorization_code);
        parameters.redirectURI(config.getCallbackUrl());
        parameters.code(code);

        return parameters;
    }

    public OAuthParameters getClientTokenParameters() {
        final OAuthParameters parameters = new OAuthParameters();
        parameters.grantType(GrantType.client_credentials);
        parameters.scope(new String[] { "read", "write" });

        return parameters;
    }

    public OAuthParameters getRefreshTokenParameters(final String refreshToken) {
        final OAuthParameters parameters = new OAuthParameters();
        parameters.grantType(GrantType.refresh_token);
        parameters.refreshToken(refreshToken);

        return parameters;
    }
}
