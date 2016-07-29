package com.blackducksoftware.integration.email.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EmailDataTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testConstructor() {
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> model = new HashMap<>();
		final EmailData data = new EmailData(addresses, model);

		assertEquals(addresses, data.getAddresses());
		assertEquals(model, data.getModel());
	}

	@Test
	public void testNullAddress() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("address list is null");
		final List<String> addresses = null;
		final Map<String, Object> model = new HashMap<>();
		new EmailData(addresses, model);
	}

	@Test
	public void testNullModel() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("model is null");
		final List<String> addresses = new ArrayList<>();
		final Map<String, Object> model = null;
		new EmailData(addresses, model);
	}
}
