package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockRouter;
import com.blackducksoftware.integration.email.mock.TestEmailEngine;
import com.blackducksoftware.integration.email.notifier.EmailEngine;

public class ItemRouterTest {
	private final static String ROUTER_KEY = "router.key";
	private MockRouter router;
	private EmailEngine engine;

	@Before
	public void initTest() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("extension.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("ext.config.location", file.getCanonicalFile().getParent());
		engine = new TestEmailEngine();
		router = new MockRouter(engine.customerProperties, engine.notificationDataService, engine.extConfigDataService,
				engine.emailMessagingService, ROUTER_KEY);
	}

	@After
	public void endTest() throws Exception {
		engine.shutDown();
	}

	@Test
	public void testGetName() {
		assertEquals(MockRouter.class.getName(), router.getName());
	}

	@Test
	public void testGetRouterTemplateName() {
		assertEquals(ROUTER_KEY, router.getTemplateName());
	}

	@Test
	public void testGetRouterPropKey() {
		assertEquals(ROUTER_KEY, router.getRouterPropertyKey());
	}

	@Test
	public void testGetInterval() {
		assertEquals(MockRouter.CRON_EXPRESSION, router.getCronExpression());
	}

	@Test
	public void testGetDelay() {
		assertEquals(0, router.getStartDelayMilliseconds());
	}

	@Test
	public void testRun() {
		router.run();
		assertTrue(router.hasRun());
	}
}
