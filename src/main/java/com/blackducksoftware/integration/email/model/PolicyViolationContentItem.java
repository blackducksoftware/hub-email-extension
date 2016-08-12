package com.blackducksoftware.integration.email.model;

public class PolicyViolationContentItem extends EmailContentItem {

	private final String policyName;

	public PolicyViolationContentItem(final String projectName, final String projectVersion, final String componentName,
			final String componentVersion, final String policyName) {
		super(projectName, projectVersion, componentName, componentVersion);
		this.policyName = policyName;
	}

	public String getPolicyName() {
		return policyName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((policyName == null) ? 0 : policyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PolicyViolationContentItem other = (PolicyViolationContentItem) obj;
		if (policyName == null) {
			if (other.policyName != null) {
				return false;
			}
		} else if (!policyName.equals(other.policyName)) {
			return false;
		}
		return true;
	}
}
