package com.blackducksoftware.integration.email.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.blackducksoftware.integration.email.service.properties.ServicePropertiesBuilder;
import com.blackducksoftware.integration.email.service.properties.ServicePropertyDescriptor;

public class ServicePropertiesBuilderTest {

	@Test
	public void testGeneratePropFileDefault() throws Exception {
		final ServicePropertiesBuilder propBuilder = new ServicePropertiesBuilder();
		final Properties props = propBuilder.build();

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}

		final File generatedFile = new File(ServicePropertiesBuilder.DEFAULT_PROP_FILE_NAME);

		if (generatedFile.exists()) {
			generatedFile.delete();
		}
	}

	@Test
	public void testGeneratePropFileWithDirectoryPath() throws Exception {
		final String directory = "build/resources/test";
		final ServicePropertiesBuilder propBuilder = new ServicePropertiesBuilder();
		propBuilder.setFilePath(directory);
		final Properties props = propBuilder.build();
		final File generatedFile = new File(propBuilder.getFilePath(), ServicePropertiesBuilder.DEFAULT_PROP_FILE_NAME);

		assertTrue(generatedFile.exists());

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}

		generatedFile.delete();
	}

	@Test
	public void testGeneratePropFilePath() throws Exception {
		final String path = "build/resources/test/email.props";
		final ServicePropertiesBuilder propBuilder = new ServicePropertiesBuilder();
		propBuilder.setFilePath(path);
		final Properties props = propBuilder.build();
		final File generatedFile = new File(propBuilder.getFilePath());

		assertTrue(generatedFile.exists());

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}

		generatedFile.delete();
	}

	@Test
	public void testReadPropertiesFile() throws Exception {
		final String path = "build/resources/test/readTest.props";
		final ServicePropertiesBuilder propBuilder = new ServicePropertiesBuilder();
		propBuilder.setFilePath(path);
		final File file = new File(propBuilder.getFilePath());
		final Properties existingProps = createTestPropertiesFile(file);

		final Properties props = propBuilder.build();
		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			final String existing = existingProps.getProperty(descriptor.getKey());
			final String readProperty = props.getProperty(descriptor.getKey());
			assertEquals(existing, readProperty);
		}

		file.delete();
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

}
