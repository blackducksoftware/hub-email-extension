package com.blackducksoftware.integration.email.model;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class ExtensionPropertiesTest {

	private static final String LOCAL_VALUE = "Local Value";
	private static final String DEFAULT_VALUE = "Default Value";
	private static final String COMMON_KEY = "common_key";
	private static final String UNIQUE_KEY_DEFAULT = "default_unique_key";
	private static final String UNIQUE_KEY_LOCAL = "local_unique_key";

	private Properties createDefaults() {
		final Properties props = new Properties();
		props.put(COMMON_KEY, DEFAULT_VALUE);
		props.put(UNIQUE_KEY_DEFAULT, DEFAULT_VALUE);
		return props;
	}

	private Properties createLocal() {
		final Properties props = new Properties();
		props.put(COMMON_KEY, LOCAL_VALUE);
		props.put(UNIQUE_KEY_LOCAL, LOCAL_VALUE);
		return props;
	}

	@Test
	public void testPropertyOverride() {
		final Properties defaults = createDefaults();
		final Properties local = createLocal();

		final ExtensionProperties extProps = new ExtensionProperties(defaults, local);
		final Properties appProps = extProps.getAppProperties();
		assertEquals(3, extProps.getAppProperties().size());
		assertEquals(appProps.get(UNIQUE_KEY_DEFAULT), DEFAULT_VALUE);
		assertEquals(appProps.get(COMMON_KEY), LOCAL_VALUE);
		assertEquals(appProps.get(UNIQUE_KEY_LOCAL), LOCAL_VALUE);
	}
}
