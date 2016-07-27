package com.blackducksoftware.integration.email.service.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicePropertiesBuilder {

	private static Logger logger = LoggerFactory.getLogger(ServicePropertiesBuilder.class);
	public final static String DEFAULT_PROP_FILE_NAME = "application.properties";

	private String filePath;
	private String serviceName;
	private Map<String, String> requiredPropertyMap;

	public boolean generatePropertyFile() throws FileNotFoundException, IOException {
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
			logger.error("Properties file for the service does not exist prior to service startup.");
			logger.error("A properties file for the service has been generated at the following location: "
					+ file.getCanonicalPath());
			return true;
		} else {
			return false;
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	public Map<String, String> getRequiredPropertyMap() {
		return requiredPropertyMap;
	}

	public void setRequiredPropertyMap(final Map<String, String> requiredPropertyMap) {
		this.requiredPropertyMap = requiredPropertyMap;
	}

	private Properties generatePropertiesFile(final File file) throws IOException {
		// override and using a treeset allows the properties to appear in
		// alphabetical order when written into the properties file at the very
		// least
		final Properties props = new Properties() {
			private static final long serialVersionUID = 8459018759737070378L;

			@Override
			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			}
		};
		try (FileOutputStream output = new FileOutputStream(file)) {
			for (final ServicePropertyDescriptor descriptor : ServicePropertyDescriptor.values()) {
				props.put(descriptor.getKey(), descriptor.getDefaultValue());
			}

			if (getRequiredPropertyMap() != null && !getRequiredPropertyMap().isEmpty()) {
				final Set<String> keySet = getRequiredPropertyMap().keySet();
				for (final String key : keySet) {
					props.put(key, getRequiredPropertyMap().get(key));
				}
			}

			String comment = "Properties file for Hub extension service";
			if (StringUtils.isNotBlank(getServiceName())) {
				comment += ": " + getServiceName();
			}
			props.store(output, comment);
		}
		return props;
	}
}
