package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.EmailEngine;
import com.blackducksoftware.integration.email.mock.MockNotifier;
import com.blackducksoftware.integration.email.mock.TestEmailEngine;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;

public class ItemNotifierTest {
	private final static String NOTIFIER_KEY = "notifier.key";
	private MockNotifier notifier;
	private EmailEngine engine;

	@Before
	public void initTest() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("extension.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("ext.config.location", file.getCanonicalFile().getParent());
		engine = new TestEmailEngine();
		engine.start();
		final DataServicesFactory dataservicesFactory = new DataServicesFactory(engine.getRestConnection());
		notifier = new MockNotifier(engine.getExtensionProperties(), engine.getNotificationDataService(),
				engine.getExtConfigDataService(), engine.getEmailMessagingService(), dataservicesFactory, NOTIFIER_KEY);

	}

	@After
	public void endTest() throws Exception {
		engine.shutDown();
	}

	@Test
	public void testGetName() {
		assertEquals(MockNotifier.class.getName(), notifier.getName());
	}

	@Test
	public void testGetNotifierTemplateName() {
		assertEquals(NOTIFIER_KEY, notifier.getTemplateName());
	}

	@Test
	public void testGetNotifierPropKey() {
		assertEquals(NOTIFIER_KEY, notifier.getNotifierPropertyKey());
	}

	@Test
	public void testGetInterval() {
		assertEquals(MockNotifier.CRON_EXPRESSION, notifier.getCronExpression());
	}

	@Test
	public void testGetDelay() {
		assertEquals(0, notifier.getStartDelayMilliseconds());
	}

	@Test
	public void testRun() {
		notifier.run();
		assertTrue(notifier.hasRun());
	}
}
