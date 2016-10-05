package com.blackducksoftware.integration.email.mock;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.EmailEngine;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.NotifierManager;
import com.blackducksoftware.integration.hub.builder.HubProxyInfoBuilder;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyNotificationFilter;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.EncryptionException;
import com.blackducksoftware.integration.hub.global.HubCredentials;
import com.blackducksoftware.integration.hub.global.HubProxyInfo;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class TestEmailEngine extends EmailEngine {

	private final Logger logger = LoggerFactory.getLogger(TestEmailEngine.class);

	public TestEmailEngine() throws IOException, EncryptionException, URISyntaxException, BDRestException {
		super();
	}

	@Override
	public HubServerConfig createHubConfig() {
		HubServerConfig serverConfig = null;
		try {
			HubCredentials credentials;
			credentials = new HubCredentials("user", "password");

			final HubProxyInfo proxyInfo = new HubProxyInfoBuilder().buildResults().getConstructedObject();
			serverConfig = new HubServerConfig(new URL("http://localhost"), 120, credentials, proxyInfo);
		} catch (final EncryptionException | MalformedURLException e) {
			logger.error("Error creating hub server config", e);
		}
		return serverConfig;
	}

	@Override
	public JavaMailWrapper createJavaMailWrapper() {
		return new MockMailWrapper(false);
	}

	@Override
	public NotificationDataService createNotificationDataService() {
		return new MockNotificationDataService(restConnection, dataServicesFactory.getGson(),
				dataServicesFactory.getJsonParser(), new PolicyNotificationFilter(null));
	}

	@Override
	public RestConnection createRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = new RestConnection(hubServerConfig.getHubUrl().toString());

		// restConnection.setCookies(hubServerConfig.getGlobalCredentials().getUsername(),
		// hubServerConfig.getGlobalCredentials().getDecryptedPassword());
		restConnection.setProxyProperties(hubServerConfig.getProxyInfo());
		restConnection.setTimeout(hubServerConfig.getTimeout());
		return restConnection;
	}

	@Override
	public NotifierManager createNotifierManager() {
		final NotifierManager manager = new NotifierManager();
		final TestDigestNotifier digestNotifier = new TestDigestNotifier(customerProperties, notificationDataService,
				extConfigDataService, emailMessagingService);
		manager.attach(digestNotifier);
		return manager;
	}

	@Override
	public void start() {
		notifierManager.start();
	}
}
