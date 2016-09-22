package com.blackducksoftware.integration.email.mock;

import java.io.IOException;
import java.net.URISyntaxException;

import com.blackducksoftware.integration.email.model.FileMailWrapper;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.notifier.EmailEngine;
import com.blackducksoftware.integration.email.notifier.routers.RouterManager;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.EncryptionException;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class TestEmailEngine extends EmailEngine {

	public TestEmailEngine() throws IOException, EncryptionException, URISyntaxException, BDRestException {
		super();
	}

	@Override
	public JavaMailWrapper createJavaMailWrapper() {
		return new FileMailWrapper();
	}

	@Override
	public RestConnection createRestConnection() throws EncryptionException, URISyntaxException, BDRestException {
		final RestConnection restConnection = new RestConnection(hubServerConfig.getHubUrl().toString());

		restConnection.setCookies(hubServerConfig.getGlobalCredentials().getUsername(),
				hubServerConfig.getGlobalCredentials().getDecryptedPassword());
		restConnection.setProxyProperties(hubServerConfig.getProxyInfo());
		restConnection.setTimeout(hubServerConfig.getTimeout());
		return restConnection;
	}

	@Override
	public RouterManager createRouterManager() {
		final RouterManager manager = new RouterManager();
		final TestDigestRouter digestRouter = new TestDigestRouter(customerProperties, notificationDataService,
				userRestService, emailMessagingService);
		manager.attachRouter(digestRouter);
		return manager;
	}

	@Override
	public void start() {
		super.start();
		routerManager.startRouters();
	}
}
