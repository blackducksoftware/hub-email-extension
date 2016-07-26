package com.blackducksoftware.integration.email.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.service.properties.ServicePropertiesBuilder;
import com.blackducksoftware.integration.email.service.properties.ServicePropertyDescriptor;

public class ServicePropertiesBuilderTest {
	private File generatedFile;
	private ServicePropertiesBuilder propBuilder;

	@Before
	public void initTest() {
		propBuilder = new ServicePropertiesBuilder();
	}

	@After
	public void deleteGeneratedFile() {
		if (generatedFile.exists()) {
			generatedFile.delete();
		}
	}

	private Properties createTestPropertiesFile(final File file) throws FileNotFoundException, IOException {
		final Properties props = new Properties();
		try (final FileOutputStream output = new FileOutputStream(file)) {
			for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
				// save the key as the value to be different than default value
				// for testing.
				props.put(descriptor.getKey(), descriptor.getKey());
			}
			props.store(output, "Generated for unit test");
		}

		return props;
	}

	private Properties readTestProperties(final File file) throws FileNotFoundException, IOException {
		final Properties props = new Properties();
		try (FileInputStream input = new FileInputStream(file)) {
			props.load(input);
		}
		return props;
	}

	@Test
	public void testGeneratePropFileDefault() throws Exception {
		propBuilder.setFilePath(null);
		final boolean exists = propBuilder.propertyFileExists();
		assertFalse(exists);
		generatedFile = new File(propBuilder.getFilePath());
		final Properties props = readTestProperties(generatedFile);
		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}
	}

	@Test
	public void testGeneratePropFileWithDirectoryPath() throws Exception {
		propBuilder.setFilePath("build/resources/test");
		final boolean exists = propBuilder.propertyFileExists();
		assertFalse(exists);
		generatedFile = new File(propBuilder.getFilePath());
		final Properties props = readTestProperties(generatedFile);

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}
	}

	@Test
	public void testGeneratePropFilePath() throws Exception {
		propBuilder.setFilePath("build/resources/test/email.props");
		final boolean exists = propBuilder.propertyFileExists();
		assertFalse(exists);
		generatedFile = new File(propBuilder.getFilePath());
		final Properties props = readTestProperties(generatedFile);
		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}
	}

	@Test
	public void testReadPropertiesFile() throws Exception {
		propBuilder.setFilePath("build/resources/test/readTest.props");
		generatedFile = new File(propBuilder.getFilePath());
		final Properties existingProps = createTestPropertiesFile(generatedFile);
		final boolean exists = propBuilder.propertyFileExists();
		assertTrue(exists);
		final Properties props = readTestProperties(generatedFile);
		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			final String existing = existingProps.getProperty(descriptor.getKey());
			final String readProperty = props.getProperty(descriptor.getKey());
			assertEquals(existing, readProperty);
		}
	}

	@Test
	public void testReadDefaultPropertiesFile() throws Exception {
		generatedFile = new File(ServicePropertiesBuilder.DEFAULT_PROP_FILE_NAME);
		final Properties existingProps = createTestPropertiesFile(generatedFile);
		final boolean exists = propBuilder.propertyFileExists();
		assertTrue(exists);
		final Properties props = readTestProperties(generatedFile);
		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			final String existing = existingProps.getProperty(descriptor.getKey());
			final String readProperty = props.getProperty(descriptor.getKey());
			assertEquals(existing, readProperty);
		}
	}
}
