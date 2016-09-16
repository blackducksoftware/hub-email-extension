package com.blackducksoftware.integration.email.extension.server.api.model;

import org.json.JSONObject;

public class ExtensionConfigurationItem {
	private String extensionUrl;
	private String hubBaseUrl;
	private String oAuthAuthorizeUrl;
	private String oAuthTokenUrl;

	public ExtensionConfigurationItem() {
	}

	public ExtensionConfigurationItem(final JSONObject json) {
		this(json.getString("extensionUrl"), json.getString("hubBaseUrl"), json.getString("oAuthAuthorizeUrl"),
				json.getString("oAuthTokenUrl"));
	}

	public ExtensionConfigurationItem(final String extensionUrl, final String hubBaseUrl,
			final String oAuthAuthorizeUrl, final String oAuthTokenUrl) {
		this.extensionUrl = extensionUrl;
		this.hubBaseUrl = hubBaseUrl;
		this.oAuthAuthorizeUrl = oAuthAuthorizeUrl;
		this.oAuthTokenUrl = oAuthTokenUrl;
	}

	public String getExtensionUrl() {
		return extensionUrl;
	}

	public void setExtensionUrl(final String extensionUrl) {
		this.extensionUrl = extensionUrl;
	}

	public String getHubBaseUrl() {
		return hubBaseUrl;
	}

	public void setHubBaseUrl(final String hubBaseUrl) {
		this.hubBaseUrl = hubBaseUrl;
	}

	public String getoAuthAuthorizeUrl() {
		return oAuthAuthorizeUrl;
	}

	public void setoAuthAuthorizeUrl(final String oAuthAuthorizeUrl) {
		this.oAuthAuthorizeUrl = oAuthAuthorizeUrl;
	}

	public String getoAuthTokenUrl() {
		return oAuthTokenUrl;
	}

	public void setoAuthTokenUrl(final String oAuthTokenUrl) {
		this.oAuthTokenUrl = oAuthTokenUrl;
	}
}
