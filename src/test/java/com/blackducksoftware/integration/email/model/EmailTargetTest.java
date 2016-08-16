package com.blackducksoftware.integration.email.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EmailTargetTest {

	private static final String TEMPLATE = "template.ftl";
	private static final String ADDRESS = "address@domain.com";
	private static final String MODEL_KEY = "model_key";
	private static final String MODEL_ITEM = "model_item";

	@Test
	public void testConstructor() {
		final Map<String, Object> map = new HashMap<>();
		map.put(MODEL_KEY, MODEL_ITEM);
		final EmailTarget target = new EmailTarget(ADDRESS, TEMPLATE, map);
		assertEquals(ADDRESS, target.getEmailAddress());
		assertEquals(TEMPLATE, target.getTemplateName());
		assertEquals(map, target.getModel());
		assertTrue(target.getModel().containsKey(MODEL_KEY));
		assertEquals(MODEL_ITEM, target.getModel().get(MODEL_KEY));
	}
}
