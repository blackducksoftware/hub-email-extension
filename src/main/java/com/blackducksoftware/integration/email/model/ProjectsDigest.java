package com.blackducksoftware.integration.email.model;

import java.util.Map;

public class ProjectsDigest {

	private final Map<String, String> totalsMap;
	private final FreemarkerTarget projectList;

	public ProjectsDigest(final Map<String, String> totalsMap, final FreemarkerTarget projectList) {
		this.totalsMap = totalsMap;
		this.projectList = projectList;
	}

	public Map<String, String> getTotalsMap() {
		return totalsMap;
	}

	public FreemarkerTarget getProjectList() {
		return projectList;
	}
}
