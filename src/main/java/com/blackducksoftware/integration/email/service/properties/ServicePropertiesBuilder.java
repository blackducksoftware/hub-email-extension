package com.blackducksoftware.integration.email.service.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

public class ServicePropertiesBuilder {

	public final static String DEFAULT_PROP_FILE_NAME = "service.properties";

	private String filePath;

	public Properties build() throws FileNotFoundException, IOException {
		File file;
		if (StringUtils.isNotBlank(filePath)) {
			file = new File(filePath);
		} else {
			file = new File(DEFAULT_PROP_FILE_NAME);
		}

		if (file.isDirectory()) {
			if (file.isDirectory()) {
				file = new File(file, DEFAULT_PROP_FILE_NAME);
			}
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

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	private Properties generatePropertiesFile(final File file) throws IOException {
		final Properties props = new Properties();
		try (FileOutputStream output = new FileOutputStream(file)) {
			for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
				props.put(descriptor.getKey(), descriptor.getDefaultValue());
			}
			props.store(output, createFileComment());
		}
		return props;
	}

	private String createFileComment() {
		final String dateString = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date());
		return "Hub extension service properties file generated on: " + dateString;
	}

	private Properties readProperties(final File file) throws FileNotFoundException, IOException {
		final Properties props = new Properties();
		try (FileInputStream input = new FileInputStream(file)) {
			props.load(input);
		}
		return props;
	}
}
