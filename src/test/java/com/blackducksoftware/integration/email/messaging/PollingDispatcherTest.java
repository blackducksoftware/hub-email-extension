package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockDispatcher;
import com.blackducksoftware.integration.email.mock.MockRouter;

public class PollingDispatcherTest {

	private final static String CONFIG_DATA = "config data string";
	private final static String RECEIVE_DATA = "receive data string";
	private final static String SEND_DATA = "send data string";
	private Set<String> topicSet;
	private MockRouter router;
	private MockDispatcher dispatcher;
	private ExecutorService executorService;

	@Before
	public void intTest() {
		topicSet = new HashSet<>();
		topicSet.add("topic1");
		topicSet.add("topic2");
		topicSet.add("topic3");

		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
	}

	@Test
	public void testInitialization() {
		dispatcher = new MockDispatcher(topicSet, true, RECEIVE_DATA);
		dispatcher.init();
		assertEquals(MockDispatcher.NAME, dispatcher.getName());
		assertEquals(MockDispatcher.INTERVAL, dispatcher.getInterval());
		assertEquals(MockDispatcher.DELAY, dispatcher.getStartupDelay());
		assertNull(dispatcher.getCurrentRun());
		assertNull(dispatcher.getLastRun());
	}

	@Test
	public void testRouterReceive() throws Exception {
		router = new MockRouter(topicSet, CONFIG_DATA, RECEIVE_DATA, SEND_DATA);
		dispatcher = new MockDispatcher(topicSet, true, RECEIVE_DATA);
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

	@Test
	public void testRouterConfigure() throws Exception {
		router = new MockRouter(topicSet, CONFIG_DATA, RECEIVE_DATA, SEND_DATA);
		dispatcher = new MockDispatcher(topicSet, false, CONFIG_DATA);
		dispatcher.init();
		dispatcher.setExecutorService(executorService);
		dispatcher.attachRouter(router);
		dispatcher.start();

		// wait for the dispatcher to send it's message to the router
		// router will assert equals
		Thread.sleep((MockDispatcher.INTERVAL + MockDispatcher.DELAY) * 2);

		assertNotNull(dispatcher.getCurrentRun());
		assertNotNull(dispatcher.getLastRun());

		dispatcher.stop();
	}
}
