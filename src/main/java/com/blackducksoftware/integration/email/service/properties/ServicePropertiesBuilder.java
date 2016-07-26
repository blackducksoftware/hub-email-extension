package com.blackducksoftware.integration.email.service.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class ServicePropertiesBuilder {

	public final static String DEFAULT_PROP_FILE_NAME = "application.properties";

	private String filePath;

	public boolean propertyFileExists() throws FileNotFoundException, IOException {
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
			generatePropertiesFile(file);
			setFilePath(file.getCanonicalPath());
			return false;
		} else {
			return true;
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
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
}
