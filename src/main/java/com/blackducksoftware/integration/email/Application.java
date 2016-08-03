package com.blackducksoftware.integration.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.email.notifier.NotificationDispatcher;
import com.blackducksoftware.integration.email.notifier.routers.factory.AbstractEmailFactory;
import com.blackducksoftware.integration.email.notifier.routers.factory.PolicyViolationFactory;
import com.blackducksoftware.integration.email.notifier.routers.factory.PolicyViolationOverrideCancelFactory;
import com.blackducksoftware.integration.email.notifier.routers.factory.PolicyViolationOverrideFactory;
import com.blackducksoftware.integration.email.notifier.routers.factory.VulnerabilityFactory;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.service.properties.HubServerBeanConfiguration;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class Application {

	private final RestTemplate restTemplate;
	private final Gson gson;
	private final Configuration configuration;
	private final DateFormat notificationDateFormat;
	private final Date applicationStartDate;
	private final ExecutorService executorService;

	private final List<AbstractEmailFactory> routerFactoryList;
	private final EmailSystemProperties emailSystemProperties;
	private final EmailMessagingService emailMessagingService;
	private final NotificationDispatcher notificationDispatcher;
	private final HubServerConfig hubServerConfig;
	private final Properties appProperties;
	private final CustomerProperties customerProperties;

	public static void main(final String[] args) {
		try {
			new Application();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public Application() throws IOException {
		restTemplate = new RestTemplate();
		gson = new Gson();
		appProperties = createAppProperties();
		emailSystemProperties = createEmailSystemProperties();
		customerProperties = createCustomerProperties();
		configuration = createFreemarkerConfig();
		hubServerConfig = createHubConfig();

		notificationDateFormat = createNotificationDateFormat();
		applicationStartDate = createApplicationStartDate();
		executorService = createExecutorService();
		emailMessagingService = createEmailMessagingService();
		routerFactoryList = createRouterFactoryList();
		notificationDispatcher = createDispatcher();

		notificationDispatcher.init();
		notificationDispatcher.attachRouters(routerFactoryList);
		notificationDispatcher.start();
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
		cfg.setDirectoryForTemplateLoading(new File("../../../src/main/resources/templates/"));
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

	private EmailSystemProperties createEmailSystemProperties() {
		return new EmailSystemProperties(appProperties);
	}

	private CustomerProperties createCustomerProperties() {

		return new CustomerProperties(appProperties);
	}

	private EmailMessagingService createEmailMessagingService() {
		return new EmailMessagingService(emailSystemProperties, configuration);
	}

	private List<AbstractEmailFactory> createRouterFactoryList() {
		final List<AbstractEmailFactory> factoryList = new Vector<>();
		factoryList.add(new PolicyViolationFactory(emailMessagingService, customerProperties));
		factoryList.add(new PolicyViolationOverrideCancelFactory(emailMessagingService, customerProperties));
		factoryList.add(new PolicyViolationOverrideFactory(emailMessagingService, customerProperties));
		factoryList.add(new VulnerabilityFactory(emailMessagingService, customerProperties));

		return factoryList;
	}

	private HubServerConfig createHubConfig() {
		final HubServerBeanConfiguration serverBeanConfig = new HubServerBeanConfiguration(emailSystemProperties);

		return serverBeanConfig.build();
	}

	private NotificationDispatcher createDispatcher() {
		return new NotificationDispatcher(hubServerConfig, notificationDateFormat, applicationStartDate,
				emailSystemProperties, executorService);
	}
}
