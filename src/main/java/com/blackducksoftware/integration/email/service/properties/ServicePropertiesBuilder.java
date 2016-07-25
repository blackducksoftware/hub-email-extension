package com.blackducksoftware.integration.email.service.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServicePropertiesBuilder {

	public final static String DEFAULT_PROP_FILE_NAME = "service.properties";

	@Value("${propertyFile}")
	private String filePath;

	@PostConstruct
	public Properties build() throws FileNotFoundException, IOException {
		File file;
		if (StringUtils.isNotBlank(getFilePath())) {
			file = new File(getFilePath());
		} else {
			file = new File(DEFAULT_PROP_FILE_NAME);
		}

		if (file.isDirectory()) {
			file = new File(file, DEFAULT_PROP_FILE_NAME);
		}

		if (!file.exists()) {
			return generatePropertiesFile(file);
		} else {
			return readProperties(file);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	private Properties generatePropertiesFile(final File file) throws IOException {
		final Properties props = new Properties();
		try (FileOutputStream output = new FileOutputStream(file)) {
			for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
				props.put(descriptor.getKey(), descriptor.getDefaultValue());
			}
			props.store(output, "Hub extension service properties file.");
		}
		return props;
	}

	private Properties readProperties(final File file) throws FileNotFoundException, IOException {
		final Properties props = new Properties();
		try (FileInputStream input = new FileInputStream(file)) {
			props.load(input);
		}
		return props;
	}
}
