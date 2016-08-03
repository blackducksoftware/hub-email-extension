package com.blackducksoftware.integration.email;

import java.io.File;
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

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

@SpringBootApplication
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

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public Application() throws IOException {
		restTemplate = new RestTemplate();
		gson = new Gson();
		configuration = createFreemarkerConfig();
		notificationDateFormat = createNotificationDateFormat();
		applicationStartDate = createApplicationStartDate();
		executorService = createExecutorService();
		emailMessagingService = createEmailMessagingService();
		routerFactoryList = createRouterFactoryList();
		emailSystemProperties = createEmailSystemProperties();
		notificationDispatcher = createDispatcher();
		hubServerConfig = createHubConfig();

		notificationDispatcher.attachRouters(routerFactoryList);
		notificationDispatcher.start();
	}

	private Configuration createFreemarkerConfig() throws IOException {
		final Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setDirectoryForTemplateLoading(
				new File("/Users/ekerwin/Documents/bitbucket/hub-email-extension/src/main/resources/templates/"));
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

	private Properties createPropertiesFile() {
		return new Properties();
	}

	private EmailSystemProperties createEmailSystemProperties() {
		return new EmailSystemProperties(createPropertiesFile());
	}

	private EmailMessagingService createEmailMessagingService() {
		return new EmailMessagingService(emailSystemProperties, configuration);
	}

	private List<AbstractEmailFactory> createRouterFactoryList() {
		final List<AbstractEmailFactory> factoryList = new Vector<>();
		factoryList.add(new PolicyViolationFactory(emailMessagingService));
		factoryList.add(new PolicyViolationOverrideCancelFactory(emailMessagingService));
		factoryList.add(new PolicyViolationOverrideFactory(emailMessagingService));
		factoryList.add(new VulnerabilityFactory(emailMessagingService));

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
