package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockRouter;
import com.blackducksoftware.integration.email.notifier.EmailEngine;

public class ItemRouterTest {
	private final static String ROUTER_KEY = "router.key";
	private MockRouter router;
	private EmailEngine engine;

	@Before
	public void initTest() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("test.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("customer.properties", file.getCanonicalPath());
		engine = new EmailEngine();
		router = new MockRouter(engine.customerProperties, engine.notificationDataService, engine.userRestService,
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
	public void testGetRouterKey() {
		assertEquals(ROUTER_KEY, router.getRouterKey());
	}

	@Test
	public void testGetInterval() {
		assertEquals(MockRouter.ROUTER_INTERVAL, router.getIntervalMilliseconds());
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
