package com.blackducksoftware.integration.email.notifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.ExtensionLogger;
import com.blackducksoftware.integration.email.extension.model.ExtensionInfoData;
import com.blackducksoftware.integration.email.extension.server.RestletApplication;
import com.blackducksoftware.integration.email.extension.server.oauth.AccessType;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthEndpoint;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthRestConnection;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.extension.server.oauth.listeners.IAuthorizedListener;
import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.HubServerBeanConfiguration;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.routers.DailyDigestRouter;
import com.blackducksoftware.integration.email.notifier.routers.MonthlyDigestRouter;
import com.blackducksoftware.integration.email.notifier.routers.RouterManager;
import com.blackducksoftware.integration.email.notifier.routers.WeeklyDigestRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.EncryptionException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EmailEngine implements IAuthorizedListener {
	private final Logger logger = LoggerFactory.getLogger(EmailEngine.class);

	public final Gson gson;
	public final JsonParser jsonParser;
	public final Configuration configuration;
	public final DateFormat notificationDateFormat;
	public final Date applicationStartDate;
	public final ExecutorService executorService;
	public final JavaMailWrapper javaMailWrapper;

	public final EmailMessagingService emailMessagingService;
	public final HubServerConfig hubServerConfig;
	public final RestConnection restConnection;
	public final Properties appProperties;
	public final CustomerProperties customerProperties;
	public final NotificationDataService notificationDataService;
	public final UserRestService userRestService;
	public final RouterManager routerManager;
	public final TokenManager tokenManager;
	public final OAuthEndpoint restletComponent;
	public final ExtensionInfoData extensionInfoData;

	public EmailEngine() throws IOException, EncryptionException, URISyntaxException, BDRestException {
		gson = new Gson();
		jsonParser = new JsonParser();
		appProperties = createAppProperties();
		customerProperties = createCustomerProperties();
		configuration = createFreemarkerConfig();
		hubServerConfig = createHubConfig();
		extensionInfoData = createExtensionInfoData();
		tokenManager = createTokenManager();
		restConnection = createRestConnection();
		javaMailWrapper = createJavaMailWrapper();
		notificationDateFormat = createNotificationDateFormat();
		applicationStartDate = createApplicationStartDate();
		executorService = createExecutorService();
		emailMessagingService = createEmailMessagingService();
		notificationDataService = createNotificationDataService();
		userRestService = createUserRestService();
		routerManager = createRouterManager();
		restletComponent = createRestletComponent();
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
			routerManager.stopRouters();
			restletComponent.stop();
		} catch (final Exception e) {
			logger.error("Error stopping Email Engine", e);
		}
	}

	public Properties createAppProperties() throws IOException {
		final Properties appProperties = new Properties();
		final String customerPropertiesPath = System.getProperty("customer.properties");
		final File customerPropertiesFile = new File(customerPropertiesPath);
		try (FileInputStream fileInputStream = new FileInputStream(customerPropertiesFile)) {
			appProperties.load(fileInputStream);
		}

		return appProperties;
	}

	public Configuration createFreemarkerConfig() throws IOException {
		final Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setDirectoryForTemplateLoading(new File(customerProperties.getEmailTemplateDirectory()));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setLogTemplateExceptions(false);

		return cfg;
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

	public CustomerProperties createCustomerProperties() {
		return new CustomerProperties(appProperties);
	}

	public JavaMailWrapper createJavaMailWrapper() {
		return new JavaMailWrapper();
	}

	public EmailMessagingService createEmailMessagingService() {
		return new EmailMessagingService(customerProperties, configuration, javaMailWrapper);
	}

	public HubServerConfig createHubConfig() {
		final HubServerBeanConfiguration serverBeanConfig = new HubServerBeanConfiguration(customerProperties);

		return serverBeanConfig.build();
	}

	public RestConnection createRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = initRestConnection();
		return restConnection;
	}

	public UserRestService createUserRestService() {
		final UserRestService userRestService = new UserRestService(restConnection, gson, jsonParser);
		return userRestService;
	}

	public NotificationDataService createNotificationDataService() {
		final Logger notificationLogger = LoggerFactory.getLogger(NotificationDataService.class);
		final ExtensionLogger serviceLogger = new ExtensionLogger(notificationLogger);
		final NotificationDataService notificationDataService = new NotificationDataService(serviceLogger,
				restConnection, gson, jsonParser);
		return notificationDataService;
	}

	public RestConnection initRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = new OAuthRestConnection(hubServerConfig.getHubUrl().toString(),
				tokenManager);
		restConnection.setProxyProperties(hubServerConfig.getProxyInfo());
		restConnection.setTimeout(hubServerConfig.getTimeout());
		return restConnection;
	}

	public RouterManager createRouterManager() {
		final RouterManager manager = new RouterManager();

		final DailyDigestRouter dailyRouter = new DailyDigestRouter(customerProperties, notificationDataService,
				userRestService, emailMessagingService);
		final WeeklyDigestRouter weeklyRouter = new WeeklyDigestRouter(customerProperties, notificationDataService,
				userRestService, emailMessagingService);
		final MonthlyDigestRouter monthlyRouter = new MonthlyDigestRouter(customerProperties, notificationDataService,
				userRestService, emailMessagingService);
		manager.attachRouter(dailyRouter);
		manager.attachRouter(weeklyRouter);
		manager.attachRouter(monthlyRouter);
		return manager;
	}

	public ExtensionInfoData createExtensionInfoData() {
		final String id = customerProperties.getExtensionId();
		final String name = customerProperties.getExtensionName();
		final String description = customerProperties.getExtensionDescription();
		final String baseUrl = customerProperties.getExtensionBaseUrl();
		final int port = customerProperties.getExtensionPort();

		return new ExtensionInfoData(id, name, description, baseUrl, port);
	}

	public OAuthEndpoint createRestletComponent() {
		final RestletApplication application = new RestletApplication(tokenManager);
		final OAuthEndpoint endpoint = new OAuthEndpoint(application);
		if (extensionInfoData.getPort() > 0) {
			endpoint.getServers().add(Protocol.HTTP, extensionInfoData.getPort());
		}

		return endpoint;
	}

	public TokenManager createTokenManager() {
		final TokenManager tokenManager = new TokenManager(extensionInfoData);
		tokenManager.addAuthorizedListener(this);
		return tokenManager;
	}

	@Override
	public void onAuthorized() {
		routerManager.startRouters();
	}
}
