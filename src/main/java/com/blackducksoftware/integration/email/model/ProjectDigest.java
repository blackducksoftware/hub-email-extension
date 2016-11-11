package com.blackducksoftware.integration.email.model;

import java.util.HashMap;
import java.util.Map;

public class ProjectDigest {
    private final Map<String, String> projectData;

    private final Map<String, FreemarkerTarget> categoryMap;

    private final FreemarkerTarget policyViolations;

    private final FreemarkerTarget policyOverrides;

    private final FreemarkerTarget vulnerabilities;

    public ProjectDigest(final Map<String, String> projectData, final FreemarkerTarget policyViolations,
            final FreemarkerTarget policyOverrides, final FreemarkerTarget vulnerabilities) {
        this.projectData = projectData;
        this.policyViolations = policyViolations;
        this.policyOverrides = policyOverrides;
        this.vulnerabilities = vulnerabilities;
        this.categoryMap = new HashMap<>();
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

    public FreemarkerTarget getVulnerabilities() {
        return vulnerabilities;
    }
}
