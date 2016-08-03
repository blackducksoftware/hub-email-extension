package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockDispatcher;
import com.blackducksoftware.integration.email.mock.MockRouterFactory;

public class PollingDispatcherTest {

	private final static String RECEIVE_DATA = "receive data string";
	private MockRouterFactory router;
	private MockDispatcher dispatcher;
	private ExecutorService executorService;

	@Before
	public void intTest() {
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	@Test
	public void testInitialization() {
		dispatcher = new MockDispatcher();
		dispatcher.init();
		assertEquals(MockDispatcher.NAME, dispatcher.getName());
		assertEquals(MockDispatcher.INTERVAL, dispatcher.getInterval());
		assertEquals(MockDispatcher.DELAY, dispatcher.getStartupDelay());
		assertNull(dispatcher.getCurrentRun());
		assertNull(dispatcher.getLastRun());
	}

	@Test
	public void testRouterExecution() throws Exception {
		router = new MockRouterFactory(null, null, RECEIVE_DATA);
		dispatcher = new MockDispatcher();
		dispatcher.init();
		dispatcher.setExecutorService(executorService);
		dispatcher.attachRouter(router);
		dispatcher.start();

		// wait for the dispatcher to send it's message
		// router will assert equals
		Thread.sleep((MockDispatcher.INTERVAL + MockDispatcher.DELAY) * 2);

		assertNotNull(dispatcher.getCurrentRun());
		assertNotNull(dispatcher.getLastRun());

		dispatcher.stop();
	}
}
