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

import com.blackducksoftware.integration.email.extension.model.ExtensionInfoData;
import com.blackducksoftware.integration.email.extension.server.RestletApplication;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthEndpoint;
import com.blackducksoftware.integration.email.extension.server.oauth.OAuthRestConnection;
import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;
import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.FileMailWrapper;
import com.blackducksoftware.integration.email.model.HubServerBeanConfiguration;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.routers.DigestRouter;
import com.blackducksoftware.integration.email.notifier.routers.RouterManager;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyNotificationFilter;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.EncryptionException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class EmailEngine {
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
			routerManager.startRouters();
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

	private Properties createAppProperties() throws IOException {
		final Properties appProperties = new Properties();
		final String customerPropertiesPath = System.getProperty("customer.properties");
		final File customerPropertiesFile = new File(customerPropertiesPath);
		try (FileInputStream fileInputStream = new FileInputStream(customerPropertiesFile)) {
			appProperties.load(fileInputStream);
		}

		return appProperties;
	}

	private Configuration createFreemarkerConfig() throws IOException {
		final Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setDirectoryForTemplateLoading(new File(customerProperties.getEmailTemplateDirectory()));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setLogTemplateExceptions(false);

		return cfg;
	}

	private DateFormat createNotificationDateFormat() {
		final DateFormat dateFormat = new SimpleDateFormat(RestConnection.JSON_DATE_FORMAT);
		dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
		return dateFormat;
	}

	private Date createApplicationStartDate() {
		return new Date();
	}

	private ExecutorService createExecutorService() {
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	private CustomerProperties createCustomerProperties() {
		return new CustomerProperties(appProperties);
	}

	private JavaMailWrapper createJavaMailWrapper() {
		return new FileMailWrapper();
	}

	private EmailMessagingService createEmailMessagingService() {
		return new EmailMessagingService(customerProperties, configuration, javaMailWrapper);
	}

	private HubServerConfig createHubConfig() {
		final HubServerBeanConfiguration serverBeanConfig = new HubServerBeanConfiguration(customerProperties);

		return serverBeanConfig.build();
	}

	private RestConnection createRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = initRestConnection();
		return restConnection;
	}

	private UserRestService createUserRestService() {
		final UserRestService userRestService = new UserRestService(restConnection, gson, jsonParser);
		return userRestService;
	}

	private NotificationDataService createNotificationDataService() {
		final NotificationDataService notificationDataService = new NotificationDataService(restConnection, gson,
				jsonParser, new PolicyNotificationFilter(null));
		return notificationDataService;
	}

	private RestConnection initRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = new OAuthRestConnection(hubServerConfig.getHubUrl().toString(),
				tokenManager);

		// restConnection.setCookies(hubServerConfig.getGlobalCredentials().getUsername(),
		// hubServerConfig.getGlobalCredentials().getDecryptedPassword());
		restConnection.setProxyProperties(hubServerConfig.getProxyInfo());

		restConnection.setTimeout(hubServerConfig.getTimeout());
		return restConnection;
	}

	private RouterManager createRouterManager() {
		final RouterManager manager = new RouterManager();

		final DigestRouter digestRouter = new DigestRouter(customerProperties, notificationDataService, userRestService,
				emailMessagingService);
		manager.attachRouter(digestRouter);
		return manager;
	}

	private ExtensionInfoData createExtensionInfoData() {
		final String id = customerProperties.getExtensionId();
		final String name = customerProperties.getExtensionName();
		final String description = customerProperties.getExtensionDescription();
		final String baseUrl = customerProperties.getExtensionBaseUrl();
		final int port = customerProperties.getExtensionPort();

		return new ExtensionInfoData(id, name, description, baseUrl, port);
	}

	private OAuthEndpoint createRestletComponent() {
		final RestletApplication application = new RestletApplication(tokenManager);
		final OAuthEndpoint endpoint = new OAuthEndpoint(application);
		if (extensionInfoData.getPort() > 0) {
			endpoint.getServers().add(Protocol.HTTP, extensionInfoData.getPort());
		}

		return endpoint;

	}

	private TokenManager createTokenManager() {
		return new TokenManager(extensionInfoData);
	}
}
