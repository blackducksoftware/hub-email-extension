package com.blackducksoftware.integration.email.messaging;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockRouter;
import com.blackducksoftware.integration.email.notifier.EmailEngine;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;

public class ItemRouterTest {
	private final static String RECEIVE_DATA = "receive data string";
	private MockRouter router;
	private EmailEngine engine;

	@Before
	public void initTest() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("test.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("customer.properties", file.getCanonicalPath());
		engine = new EmailEngine();
		final List<Object> data = new ArrayList<>();
		data.add(RECEIVE_DATA);
		final EmailTaskData taskData = new EmailTaskData(data);
		router = new MockRouter(engine.emailMessagingService, engine.customerProperties, engine.notificationService,
				taskData, RECEIVE_DATA);
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
