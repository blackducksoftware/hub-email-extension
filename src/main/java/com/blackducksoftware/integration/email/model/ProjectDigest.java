package com.blackducksoftware.integration.email.model;

import java.util.Map;

public class ProjectDigest {
	private final Map<String, String> projectData;
	private final FreemarkerTarget policyViolations;
	private final FreemarkerTarget policyOverrides;

	public ProjectDigest(final Map<String, String> projectData, final FreemarkerTarget policyViolations,
			final FreemarkerTarget policyOverrides) {
		this.projectData = projectData;
		this.policyViolations = policyViolations;
		this.policyOverrides = policyOverrides;
	}

	public Map<String, String> getProjectData() {
		return projectData;
	}

	public FreemarkerTarget getPolicyViolations() {
		return policyViolations;
	}

	public FreemarkerTarget getPolicyOverrides() {
		return policyOverrides;
	}
}
