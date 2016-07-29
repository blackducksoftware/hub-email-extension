package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockRouter;

public class ItemRouterTest {

	private final static String CONFIG_DATA = "config data string";
	private final static String RECEIVE_DATA = "receive data string";
	private final static String SEND_DATA = "send data string";
	private Set<String> topicSet;
	private MockRouter router;

	@Before
	public void initTest() {
		topicSet = new HashSet<>();
		topicSet.add("topic1");
		topicSet.add("topic2");
		topicSet.add("topic3");
		topicSet.add("topic4");
		topicSet.add("topic5");

		router = new MockRouter(topicSet, CONFIG_DATA, RECEIVE_DATA, SEND_DATA);
	}

	@Test
	public void testGetTopics() {
		assertEquals(topicSet, router.getTopics());
	}

	@Test
	public void testConfigure() {
		router.configure(CONFIG_DATA);
	}

	@Test
	public void testReceive() {
		router.receive(RECEIVE_DATA);
	}

	@Test
	public void testSend() {
		router.send(SEND_DATA);
	}
}
