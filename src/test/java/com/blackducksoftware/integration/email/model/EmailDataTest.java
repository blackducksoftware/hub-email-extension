package com.blackducksoftware.integration.email.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.blackducksoftware.integration.email.notifier.EmailEngine;

public class EmailDataTest {
	private static final String UNFILTERED_ADDRESS = "unfiltered@domain.com";
	private static final String TEMPLATE = "template.ftl";
	private static final String ADDRESS = "address@domain.com";

	@Rule
	public ExpectedException exception = ExpectedException.none();
	private EmailEngine engine;

	@Before
	public void init() throws Exception {
		engine = new EmailEngine();
		engine.shutDown(); // stop any loaded routers from running.
	}

	@Test
	public void testGetEmailTargets() {

		final EmailData data = new EmailData();
		assertNotNull(data.getEmailTargets());
		assertEquals(0, data.getEmailTargets().size());
	}

	@Test
	public void testAddTarget() {
		final EmailData data = new EmailData();
		data.addEmailTarget(new EmailTarget(ADDRESS, TEMPLATE, null));
		data.addEmailTarget(new EmailTarget(ADDRESS, TEMPLATE, null));

		assertNotNull(data.getEmailTargets());
		assertEquals(2, data.getEmailTargets().size());

		for (final EmailTarget target : data.getEmailTargets()) {
			assertEquals(ADDRESS, target.getEmailAddress());
			assertEquals(TEMPLATE, target.getTemplateName());
			assertNull(target.getModel());
		}
	}

	@Test
	public void testFilterOptOut() {
		final EmailData data = new EmailData();
		data.addEmailTarget(new EmailTarget(ADDRESS, TEMPLATE, null));
		data.addEmailTarget(new EmailTarget(UNFILTERED_ADDRESS, TEMPLATE, null));
		final UserPreferences userPreferences = new UserPreferences(engine.customerProperties);
		final EmailData filtered = data.filterOptedOutEmailAddresses(userPreferences);

		assertNotNull(filtered.getEmailTargets());
		assertEquals(1, filtered.getEmailTargets().size());

		for (final EmailTarget target : filtered.getEmailTargets()) {
			assertEquals(UNFILTERED_ADDRESS, target.getEmailAddress());
			assertEquals(TEMPLATE, target.getTemplateName());
			assertNull(target.getModel());
		}
	}
}
