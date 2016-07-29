package com.blackducksoftware.integration.email.model;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmtpConfiguration {
	// common javamail properties
	public static final String JAVAMAIL_HOST_KEY = "mail.smtp.host";
	public static final String JAVAMAIL_PORT_KEY = "mail.smtp.port";
	public static final String JAVAMAIL_AUTH_KEY = "mail.smtp.auth";
	public static final String JAVAMAIL_USER_KEY = "mail.smtp.user";

	// not a javamail property, but we are going to piggy-back on the
	// auto-parsing for javamail properties to get the password
	public static final String JAVAMAIL_PASSWORD_KEY = "mail.smtp.password";

	@Autowired
	private CustomProperties customProperties;

	public Map<String, String> getPropertiesForSession() {
		return customProperties.getSuppliedJavamailConfigProperties();
	}

	public String getHost() {
		return customProperties.getSuppliedJavamailConfigProperties().get(JAVAMAIL_HOST_KEY);
	}

	public int getPort() {
		return NumberUtils.toInt(customProperties.getSuppliedJavamailConfigProperties().get(JAVAMAIL_PORT_KEY));
	}

	public boolean isAuth() {
		return Boolean.parseBoolean(customProperties.getSuppliedJavamailConfigProperties().get(JAVAMAIL_AUTH_KEY));
	}

	public String getUsername() {
		return customProperties.getSuppliedJavamailConfigProperties().get(JAVAMAIL_USER_KEY);
	}

	public String getPassword() {
		return customProperties.getSuppliedJavamailConfigProperties().get(JAVAMAIL_PASSWORD_KEY);
	}

}
