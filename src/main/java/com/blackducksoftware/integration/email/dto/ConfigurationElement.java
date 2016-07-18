package com.blackducksoftware.integration.email.dto;

public class ConfigurationElement {
	private String name;
	private String type;
	private String[] values;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(final String[] values) {
		this.values = values;
	}

}
