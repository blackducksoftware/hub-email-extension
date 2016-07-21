package com.blackducksoftware.integration.email.dto;

import java.util.List;

public class ConfigurationElement {
	private String name;
	private String type;
	private List<String> values;

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

	public List<String> getValues() {
		return values;
	}

	public void setValues(final List<String> values) {
		this.values = values;
	}

}
