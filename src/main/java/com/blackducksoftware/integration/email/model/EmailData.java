package com.blackducksoftware.integration.email.model;

import java.util.List;
import java.util.Map;

public class EmailData {
	private final List<String> addresses;
	private final Map<String, Object> model;

	public EmailData(final List<String> addresses, final Map<String, Object> model) {
		if (addresses == null) {
			throw new IllegalArgumentException("address list is null");
		}

		if (model == null) {
			throw new IllegalArgumentException("model is null");
		}

		this.addresses = addresses;
		this.model = model;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EmailData other = (EmailData) obj;
		if (addresses == null) {
			if (other.addresses != null) {
				return false;
			}
		} else if (!addresses.equals(other.addresses)) {
			return false;
		}
		if (model == null) {
			if (other.model != null) {
				return false;
			}
		} else if (!model.equals(other.model)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EmailData [addresses=" + addresses + ", model=" + model + "]";
	}

}
