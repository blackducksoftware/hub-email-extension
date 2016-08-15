package com.blackducksoftware.integration.email.notifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.factory.AbstractEmailFactory;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.service.properties.HubServerBeanConfiguration;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.NotificationDataService;
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

	public final List<AbstractEmailFactory> routerFactoryList;
	public final EmailMessagingService emailMessagingService;
	public final NotificationDispatcher notificationDispatcher;
	public final HubServerConfig hubServerConfig;
	public final RestConnection restConnection;
	public final Properties appProperties;
	public final CustomerProperties customerProperties;
	public final NotificationDataService notificationService;
	public final Map<String, AbstractContentTransform> contentTransformMap;
	public final UserRestService userRestService;

	public EmailEngine() throws IOException, EncryptionException, URISyntaxException, BDRestException {
		gson = new Gson();
		jsonParser = new JsonParser();
		appProperties = createAppProperties();
		customerProperties = createCustomerProperties();
		configuration = createFreemarkerConfig();
		hubServerConfig = createHubConfig();
		restConnection = createRestConnection();

		notificationDateFormat = createNotificationDateFormat();
		applicationStartDate = createApplicationStartDate();
		executorService = createExecutorService();
		emailMessagingService = createEmailMessagingService();
		notificationService = createNotificationService();
		userRestService = createUserRestService();
		contentTransformMap = createContentTransformMap();
		routerFactoryList = createRouterFactoryList();
		notificationDispatcher = createDispatcher();

		notificationDispatcher.init();
		notificationDispatcher.attachRouters(routerFactoryList);
		notificationDispatcher.start();
	}

	public void shutDown() {
		notificationDispatcher.stop();
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

	private EmailMessagingService createEmailMessagingService() {
		return new EmailMessagingService(customerProperties, configuration);
	}

	@SuppressWarnings("unchecked")
	private List<AbstractEmailFactory> createRouterFactoryList() {
		final List<AbstractEmailFactory> factoryList = new Vector<>();

		final List<String> factoryClassNames = customerProperties.getFactoryClassNames();

		for (final String className : factoryClassNames) {
			Class<? extends AbstractEmailFactory> clazz;
			try {
				clazz = (Class<? extends AbstractEmailFactory>) Class.forName(className);
				final Constructor<? extends AbstractEmailFactory> constructor = clazz
						.getConstructor(EmailMessagingService.class, CustomerProperties.class, Map.class);
				factoryList
						.add(constructor.newInstance(emailMessagingService, customerProperties, contentTransformMap));
			} catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("Error initializing router factory.", e);
			}
		}
		return factoryList;
	}

	private HubServerConfig createHubConfig() {
		final HubServerBeanConfiguration serverBeanConfig = new HubServerBeanConfiguration(customerProperties);

		return serverBeanConfig.build();
	}

	private NotificationDispatcher createDispatcher() {
		return new NotificationDispatcher(hubServerConfig, applicationStartDate, customerProperties, executorService,
				notificationService, userRestService);
	}

	private RestConnection createRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = initRestConnection();
		return restConnection;
	}

	private UserRestService createUserRestService() {
		final UserRestService userRestService = new UserRestService(restConnection, gson, jsonParser);
		return userRestService;
	}

	private NotificationDataService createNotificationService()
			throws EncryptionException, URISyntaxException, BDRestException {
		if (hubServerConfig == null) {
			return new NotificationDataService(null, null, null);
		} else {
			return new NotificationDataService(restConnection, gson, jsonParser);
		}
	}

	private RestConnection initRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = new RestConnection(hubServerConfig.getHubUrl().toString());

		restConnection.setCookies(hubServerConfig.getGlobalCredentials().getUsername(),
				hubServerConfig.getGlobalCredentials().getDecryptedPassword());
		restConnection.setProxyProperties(hubServerConfig.getProxyInfo());

		restConnection.setTimeout(hubServerConfig.getTimeout());
		return restConnection;
	}

	@SuppressWarnings("unchecked")
	private Map<String, AbstractContentTransform> createContentTransformMap() {
		final Map<String, AbstractContentTransform> transformMap = new HashMap<>();
		final List<String> notificationTransformList = customerProperties.getContentTransformerList();
		for (final String className : notificationTransformList) {
			Class<? extends AbstractContentTransform> clazz;
			try {
				clazz = (Class<? extends AbstractContentTransform>) Class.forName(className);
				final Constructor<? extends AbstractContentTransform> constructor = clazz.getConstructor();
				final AbstractContentTransform transformer = constructor.newInstance();
				transformMap.put(transformer.getContentItemType(), transformer);
			} catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("Error initializing router factory.", e);
			}
		}
		return transformMap;
	}
}
