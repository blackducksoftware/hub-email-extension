package com.blackducksoftware.integration.email.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.email.Application;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.email.service.properties.ServicePropertiesBuilder;
import com.blackducksoftware.integration.email.service.properties.ServicePropertyDescriptor;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class ServicePropertiesBuilderTest {
	private File generatedFile;
	@Autowired
	private ServicePropertiesBuilder propBuilder;
	@Autowired
	private EmailSystemProperties systemProperties;

	private String oldPropFilePath;

	@Before
	public void initTest() {
		oldPropFilePath = systemProperties.getPropertyFilePath();
	}

	@After
	public void deleteGeneratedFile() {
		if (generatedFile.exists()) {
			generatedFile.delete();
		}

		systemProperties.setPropertyFilePath(oldPropFilePath);
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

	@Test
	public void testGeneratePropFileDefault() throws Exception {
		propBuilder = Mockito.spy(propBuilder);
		systemProperties.setPropertyFilePath(null);
		final Properties props = propBuilder.build();

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}
		generatedFile = new File(ServicePropertiesBuilder.DEFAULT_PROP_FILE_NAME);
	}

	@Test
	public void testGeneratePropFileWithDirectoryPath() throws Exception {
		final String directory = "build/resources/test";
		systemProperties.setPropertyFilePath(directory);
		final Properties props = propBuilder.build();
		generatedFile = new File(propBuilder.getFilePath(), ServicePropertiesBuilder.DEFAULT_PROP_FILE_NAME);

		assertTrue(generatedFile.exists());

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}
	}

	@Test
	public void testGeneratePropFilePath() throws Exception {
		final String path = "build/resources/test/email.props";
		systemProperties.setPropertyFilePath(path);
		final Properties props = propBuilder.build();
		generatedFile = new File(propBuilder.getFilePath());

		assertTrue(generatedFile.exists());

		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			assertEquals(descriptor.getDefaultValue(), props.getProperty(descriptor.getKey()));
		}
	}

	@Test
	public void testReadPropertiesFile() throws Exception {
		final String path = "build/resources/test/readTest.props";
		systemProperties.setPropertyFilePath(path);
		generatedFile = new File(propBuilder.getFilePath());
		final Properties existingProps = createTestPropertiesFile(generatedFile);

		final Properties props = propBuilder.build();
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
		systemProperties.setPropertyFilePath(null);
		final Properties props = propBuilder.build();
		for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
			final String existing = existingProps.getProperty(descriptor.getKey());
			final String readProperty = props.getProperty(descriptor.getKey());
			assertEquals(existing, readProperty);
		}
	}
}
