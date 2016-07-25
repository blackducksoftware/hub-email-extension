package com.blackducksoftware.integration.email;

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
	public final static String KEY_HUB_SERVER_URL = "hub.server.url";
	public final static String KEY_HUB_USER = "hub.server.user";
	public final static String KEY_HUB_PASSWORD = "hub.server.password";
	public final static String KEY_HUB_TIMEOUT = "hub.server.timeout";
	public final static String KEY_HUB_PROXY_HOST = "hub.proxy.host";
	public final static String KEY_HUB_PROXY_PORT = "hub.proxy.port";
	public final static String KEY_HUB_PROXY_USER = "hub.proxy.user";
	public final static String KEY_HUB_PROXY_PASS = "hub.proxy.password";
	public final static String KEY_HUB_PROXY_NO_HOST = "hub.proxy.nohost";

	private String filePath;

	public Properties build() throws FileNotFoundException, IOException {
		File file;
		if (StringUtils.isNotBlank(filePath)) {
			file = new File(filePath);
		} else {
			file = new File(DEFAULT_PROP_FILE_NAME);
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
			props.put(KEY_HUB_SERVER_URL, "http://some.hub.comany.com");
			props.put(KEY_HUB_USER, "user");
			props.put(KEY_HUB_PASSWORD, "password");
			props.put(KEY_HUB_TIMEOUT, "120");
			props.put(KEY_HUB_PROXY_HOST, "");
			props.put(KEY_HUB_PROXY_PORT, "");
			props.put(KEY_HUB_PROXY_NO_HOST, "");
			props.put(KEY_HUB_PROXY_USER, "");
			props.put(KEY_HUB_PROXY_PASS, "");
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
