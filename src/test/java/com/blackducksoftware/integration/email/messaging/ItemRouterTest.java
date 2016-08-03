package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;

public class ItemRouterTest {

	private final static String RECEIVE_DATA = "receive data string";
	private MockRouter router;

	@Before
	public void initTest() {
		router = new MockRouter(null, null, null, RECEIVE_DATA);
	}

	@Test
	public void testReceive() {
		final List<Object> dataList = new ArrayList<>();
		dataList.add(RECEIVE_DATA);
		router.execute(new EmailTaskData(dataList));
	}

	@Test
	public void testGetName() {
		assertEquals(MockRouter.ROUTER_NAME, router.getName());
	}
}
