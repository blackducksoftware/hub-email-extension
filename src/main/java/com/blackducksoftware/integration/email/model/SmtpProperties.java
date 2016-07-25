package com.blackducksoftware.integration.email.model;

import java.util.HashMap;
import java.util.Map;

public class SmtpProperties {
	private String host;
	private int port;
	private boolean auth;
	private String username;
	private String password;
	private String socketFactoryClass;
	private boolean enableTls;

	public Map<String, String> getPropertiesForSession() {
		final Map<String, String> propertiesForSession = new HashMap<>();

		propertiesForSession.put("mail.smtp.host", host);
		propertiesForSession.put("mail.smtp.port", Integer.toString(port));
		propertiesForSession.put("mail.smtp.auth", Boolean.toString(auth));

		return propertiesForSession;
	}

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(final boolean auth) {
		this.auth = auth;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getSocketFactoryClass() {
		return socketFactoryClass;
	}

	public void setSocketFactoryClass(final String socketFactoryClass) {
		this.socketFactoryClass = socketFactoryClass;
	}

	public boolean isEnableTls() {
		return enableTls;
	}

	public void setEnableTls(final boolean enableTls) {
		this.enableTls = enableTls;
	}

}
