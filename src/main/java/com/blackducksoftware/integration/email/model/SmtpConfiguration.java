package com.blackducksoftware.integration.email.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmtpConfiguration {
	@Autowired
	private EmailSystemProperties emailSystemProperties;

	public Map<String, String> getPropertiesForSession() {
		final Map<String, String> propertiesForSession = new HashMap<>();

		propertiesForSession.put("mail.smtp.host", emailSystemProperties.getSmtpHost());
		if (StringUtils.isNotBlank(emailSystemProperties.getSmtpPort())
				&& StringUtils.isNumeric(emailSystemProperties.getSmtpPort())) {
			final int port = Integer.parseInt(emailSystemProperties.getSmtpPort());
			if (port > 0) {
				propertiesForSession.put("mail.smtp.port", Integer.toString(port));
			}
		}

		if (Boolean.parseBoolean(emailSystemProperties.getSmtpAuth())) {
			propertiesForSession.put("mail.smtp.auth", "true");
		}

		return propertiesForSession;
	}

	public String getHost() {
		return emailSystemProperties.getSmtpHost();
	}

	public int getPort() {
		return Integer.parseInt(emailSystemProperties.getSmtpPort());
	}

	public boolean isAuth() {
		return Boolean.parseBoolean(emailSystemProperties.getSmtpAuth());
	}

	public String getUsername() {
		return emailSystemProperties.getSmtpUsername();
	}

	public String getPassword() {
		return emailSystemProperties.getSmtpPassword();
	}

}
