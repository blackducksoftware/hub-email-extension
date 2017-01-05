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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.StringUtils;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.config.ExtensionConfigManager;
import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.extension.server.EmailExtensionApplication;
import com.blackducksoftware.integration.email.extension.server.oauth.AccessType;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthEndpoint;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthRestConnection;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.extension.server.oauth.listeners.IAuthorizedListener;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.HubServerBeanConfiguration;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.DailyDigestNotifier;
import com.blackducksoftware.integration.email.notifier.NotifierManager;
import com.blackducksoftware.integration.email.notifier.RealTimeNotifier;
import com.blackducksoftware.integration.email.notifier.TestEmailNotifier;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.JsonParser;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EmailEngine implements IAuthorizedListener {
    private final Logger logger = LoggerFactory.getLogger(EmailEngine.class);

    private final Configuration configuration;

    private JavaMailWrapper javaMailWrapper;

    private EmailMessagingService emailMessagingService;

    private HubServerConfig hubServerConfig;

    private RestConnection restConnection;

    private final Properties appProperties;

    private final ExtensionProperties extensionProperties;

    private NotificationDataService notificationDataService;

    private NotifierManager notifierManager;

    private final TokenManager tokenManager;

    private final OAuthEndpoint restletComponent;

    private final ExtensionInfo extensionInfoData;

    private final ExtensionConfigManager extConfigManager;

    private ExtensionConfigDataService extConfigDataService;

    private DataServicesFactory dataServicesFactory;

    private EmailExtensionApplication emailExtensionApplication;

    public EmailEngine() throws IOException, EncryptionException, URISyntaxException, BDRestException {
        appProperties = createAppProperties();
        extensionProperties = createExtensionProperties();
        configuration = createFreemarkerConfig();
        extensionInfoData = createExtensionInfoData();
        tokenManager = createTokenManager();
        extConfigManager = createExtensionConfigManager();
        restletComponent = createRestletComponent();
    }

    public Logger getLogger() {
        return logger;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public JavaMailWrapper getJavaMailWrapper() {
        return javaMailWrapper;
    }

    public EmailMessagingService getEmailMessagingService() {
        return emailMessagingService;
    }

    public HubServerConfig getHubServerConfig() {
        return hubServerConfig;
    }

    public RestConnection getRestConnection() {
        return restConnection;
    }

    public Properties getAppProperties() {
        return appProperties;
    }

    public ExtensionProperties getExtensionProperties() {
        return extensionProperties;
    }

    public NotificationDataService getNotificationDataService() {
        return notificationDataService;
    }

    public NotifierManager getNotifierManager() {
        return notifierManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public OAuthEndpoint getRestletComponent() {
        return restletComponent;
    }

    public ExtensionInfo getExtensionInfoData() {
        return extensionInfoData;
    }

    public ExtensionConfigManager getExtConfigManager() {
        return extConfigManager;
    }

    public ExtensionConfigDataService getExtConfigDataService() {
        return extConfigDataService;
    }

    public DataServicesFactory getDataServicesFactory() {
        return dataServicesFactory;
    }

    public void start() {
        try {
            restletComponent.start();
            tokenManager.refreshToken(AccessType.USER);
        } catch (final Exception e) {
            logger.error("Error Starting Email Engine", e);
        }
    }

    public void shutDown() {
        try {
            notifierManager.stop();
            restletComponent.stop();
        } catch (final Exception e) {
            logger.error("Error stopping Email Engine", e);
        }
    }

    public Properties createAppProperties() throws IOException {
        final Properties appProperties = new Properties();
        final String configLocation = System.getProperty("ext.config.location");
        final File customerPropertiesFile = new File(configLocation, "extension.properties");
        try (FileInputStream fileInputStream = new FileInputStream(customerPropertiesFile)) {
            appProperties.load(fileInputStream);
        }

        return appProperties;
    }

    public Configuration createFreemarkerConfig() throws IOException {
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
        final File templateDirectory = findTemplateDirectory();
        cfg.setDirectoryForTemplateLoading(templateDirectory);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setLogTemplateExceptions(false);

        return cfg;
    }

    private File findTemplateDirectory() {
        try {
            File templateDir = null;
            final String appHomeDir = System.getProperty(EmailExtensionConstants.SYSTEM_PROPERTY_KEY_APP_HOME);
            if (StringUtils.isNotBlank(appHomeDir)) {
                templateDir = new File(appHomeDir, "templates");
            }

            final String templateDirProperty = extensionProperties.getEmailTemplateDirectory();
            if (StringUtils.isNotBlank(templateDirProperty)) {
                templateDir = new File(templateDirProperty);
            }

            return templateDir;
        } catch (final Exception e) {
            logger.error("Error finding the template directory", e);
            return null;
        }
    }

    public DateFormat createNotificationDateFormat() {
        final DateFormat dateFormat = new SimpleDateFormat(RestConnection.JSON_DATE_FORMAT);
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
        return dateFormat;
    }

    public Date createApplicationStartDate() {
        return new Date();
    }

    public ExecutorService createExecutorService() {
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
    }

    public ExtensionProperties createExtensionProperties() {
        return new ExtensionProperties(appProperties);
    }

    public JavaMailWrapper createJavaMailWrapper() {
        return new JavaMailWrapper();
    }

    public EmailMessagingService createEmailMessagingService() {
        return new EmailMessagingService(extensionProperties, configuration, javaMailWrapper);
    }

    public HubServerConfig createHubConfig(final String hubUri) {
        final HubServerBeanConfiguration serverBeanConfig = new HubServerBeanConfiguration(hubUri, extensionProperties);

        return serverBeanConfig.build();
    }

    public RestConnection createRestConnection(final String hubUri)
            throws EncryptionException, URISyntaxException, BDRestException {
        final RestConnection restConnection = initRestConnection(hubUri);
        return restConnection;
    }

    public NotificationDataService createNotificationDataService() {
        final Logger notificationLogger = LoggerFactory.getLogger(NotificationDataService.class);
        final ExtensionLogger serviceLogger = new ExtensionLogger(notificationLogger);
        final NotificationDataService notificationDataService = dataServicesFactory
                .createNotificationDataService(serviceLogger);
        return notificationDataService;
    }

    public RestConnection initRestConnection(final String hubUri)
            throws EncryptionException, URISyntaxException, BDRestException {
        final RestConnection restConnection = new OAuthRestConnection(hubServerConfig, tokenManager);
        return restConnection;
    }

    public NotifierManager createNotifierManager() {
        final NotifierManager manager = new NotifierManager();
        final DataServicesFactory dataServicesFactory = new DataServicesFactory(getRestConnection());
        final DailyDigestNotifier dailyNotifier = new DailyDigestNotifier(extensionProperties, emailMessagingService,
                dataServicesFactory, getExtensionInfoData());

        final TestEmailNotifier testNotifier = new TestEmailNotifier(extensionProperties, emailMessagingService, dataServicesFactory, getExtensionInfoData());
        final RealTimeNotifier realTimeNotifier = new RealTimeNotifier(extensionProperties, emailMessagingService, dataServicesFactory, getExtensionInfoData());
        manager.attach(dailyNotifier);
        manager.attach(testNotifier);
        manager.attach(realTimeNotifier);
        emailExtensionApplication.getContext().getAttributes().put(EmailExtensionConstants.CONTEXT_ATTRIBUTE_KEY_TEST_NOTIFIER, testNotifier);
        return manager;
    }

    public ExtensionInfo createExtensionInfoData() {

        final String id = generateExtensionId();
        final String name = extensionProperties.getExtensionName();
        final String description = extensionProperties.getExtensionDescription();
        final String baseUrl = extensionProperties.getExtensionBaseUrl();

        return new ExtensionInfo(id, name, description, baseUrl);
    }

    public String generateExtensionId() {
        final Class<? extends EmailEngine> engineClass = this.getClass();
        final Package enginePackage = engineClass.getPackage();

        final String name = enginePackage.getName();
        final String version = extensionProperties.getExtensionVersion();
        final String id = name + ".email-extension." + version;
        logger.info("Extension ID - {}", id);
        return id;
    }

    public OAuthEndpoint createRestletComponent() {
        emailExtensionApplication = new EmailExtensionApplication(tokenManager, extConfigManager);
        final OAuthEndpoint endpoint = new OAuthEndpoint(emailExtensionApplication);
        try {
            final URL url = new URL(extensionInfoData.getBaseUrl());
            final int port = url.getPort();
            if (Protocol.HTTP.getSchemeName().equals(url.getProtocol())) {
                if (port > 0) {
                    endpoint.getServers().add(Protocol.HTTP, port);
                } else {
                    endpoint.getServers().add(Protocol.HTTP);
                }
            } else if (Protocol.HTTPS.getSchemeName().equals(url.getProtocol())) {
                Server server;
                if (port > 0) {
                    server = endpoint.getServers().add(Protocol.HTTPS, port);
                } else {
                    server = endpoint.getServers().add(Protocol.HTTPS);
                }
                final Series<Parameter> parameters = server.getContext().getParameters();
                parameters.add("sslContextFactory", "org.restlet.engine.ssl.DefaultSslContextFactory");
                parameters.add("keyStorePath", extensionProperties.getSSLKeyStorePath());
                parameters.add("keyStorePassword", extensionProperties.getSSLKeyStorePassword());
                parameters.add("keyPassword", extensionProperties.getSSLKeyPassword());
                parameters.add("keyStoreType", extensionProperties.getSSLKeyStoreType());
            } else {
                logger.error("URL scheme {} not supported.  Not starting the email extension. ", url.getProtocol());
            }
        } catch (final MalformedURLException e) {
            logger.error("createRestletComponent error with base URL", e);
        }

        return endpoint;
    }

    public TokenManager createTokenManager() {
        final TokenManager tokenManager = new TokenManager(extensionInfoData);
        tokenManager.addAuthorizedListener(this);
        return tokenManager;
    }

    public ExtensionConfigManager createExtensionConfigManager() {
        // has to be separate from the dataservicesfactory otherwise we have a
        // chicken and egg problem
        final ExtensionConfigManager extConfigManager = new ExtensionConfigManager(extensionInfoData, new JsonParser());
        return extConfigManager;
    }

    public ExtensionConfigDataService createExtensionConfigDataService() {
        final Logger extensionServiceLogger = LoggerFactory.getLogger(ExtensionConfigDataService.class);
        final ExtensionLogger serviceLogger = new ExtensionLogger(extensionServiceLogger);
        final ExtensionConfigDataService extConfigDataService = dataServicesFactory
                .createExtensionConfigDataService(serviceLogger);
        return extConfigDataService;
    }

    public DataServicesFactory createDataServicesFactory() {
        return new DataServicesFactory(restConnection);
    }

    @Override
    public void onAuthorized() {
        try {
            final String hubUri = createHubBaseUrl(tokenManager.getConfiguration().getHubUri());
            hubServerConfig = createHubConfig(hubUri);
            restConnection = createRestConnection(hubUri);
            javaMailWrapper = createJavaMailWrapper();
            dataServicesFactory = createDataServicesFactory();
            emailMessagingService = createEmailMessagingService();
            notificationDataService = createNotificationDataService();
            extConfigDataService = createExtensionConfigDataService();
            notifierManager = createNotifierManager();
            notifierManager.updateHubExtensionUri(tokenManager.getConfiguration().getExtensionUri());
            notifierManager.start();
        } catch (final EncryptionException | URISyntaxException | BDRestException | MalformedURLException e) {
            logger.error("Error completing extension initialization", e);
        }
    }

    // TODO file a ticket against the hub to give me the root URL not with a
    // path
    private String createHubBaseUrl(final String hubUri) throws MalformedURLException {
        final URL original = new URL(hubUri);
        final URL baseUrl = new URL(original.getProtocol(), original.getHost(), original.getPort(), "");
        return baseUrl.toString();
    }
}
