package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockRouter;

public class ItemRouterTest {

	private final static String RECEIVE_DATA = "receive data string";
	private MockRouter router;

	@Before
	public void initTest() {

		router = new MockRouter(RECEIVE_DATA);
	}

	@Test
	public void testReceive() {
		router.execute(new RouterTaskData<String>(RECEIVE_DATA));
	}

	@Test
	public void testGetName() {
		assertEquals(MockRouter.ROUTER_NAME, router.getName());
	}
}
