package com.blackducksoftware.integration.email.model;

import java.util.List;
import java.util.Map;

public class ProjectsDigest {

    private final Map<String, String> totalsMap;

    private final List<ProjectDigest> projectList;

    public ProjectsDigest(final Map<String, String> totalsMap, final List<ProjectDigest> projectList) {
        this.totalsMap = totalsMap;
        this.projectList = projectList;
    }

    public Map<String, String> getTotalsMap() {
        return totalsMap;
    }

    public List<ProjectDigest> getProjectList() {
        return projectList;
    }
}
