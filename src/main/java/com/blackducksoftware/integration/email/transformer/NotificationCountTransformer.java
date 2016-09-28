package com.blackducksoftware.integration.email.transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.email.model.ProjectDigest;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ComponentAggregateData;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ComponentVulnerabilitySummary;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ProjectAggregateData;
import com.blackducksoftware.integration.hub.exception.MissingUUIDException;

public class NotificationCountTransformer {

	public static final String KEY_PROJECT_NAME = "projectName";
	public static final String KEY_PROJECT_VERSION = "projectVersionName";
	public static final String KEY_PROJECT_VERSION_LINK = "projectVersionLink";
	public static final String KEY_PROJECT_ID = "projectID";
	public static final String KEY_PROJECT_VERSION_ID = "projectVersionID";
	public static final String KEY_POLICY_VIOLATION_COUNT = "policyViolationCount";
	public static final String KEY_POLICY_OVERRIDE_COUNT = "policyOverrideCount";
	public static final String KEY_VULNERABILITY_COUNT = "vulnerabilityCount";
	public static final String KEY_TOTAL_NOTIFICATION_COUNT = "totalNotificationCount";
	public static final String KEY_VULN_ADDED_COUNT = "vulnAddedCount";
	public static final String KEY_VULN_UPDATED_COUNT = "vulnUpdatedCount";
	public static final String KEY_VULN_DELETED_COUNT = "vulnDeletedCount";
	public static final String KEY_VULN_HIGH_COUNT = "vulnHighCount";
	public static final String KEY_VULN_MEDIUM_COUNT = "vulnMediumCount";
	public static final String KEY_VULN_LOW_COUNT = "vulnLowCount";
	public static final String KEY_VULN_TOTAL_COUNT = "vulnTotalCount";
	public static final String KEY_COMPONENT_NAME = "componentName";
	public static final String KEY_COMPONENT_VERSION = "componentVersion";
	public static final String KEY_POLICY_NAME = "policyName";
	public static final String KEY_FIRST_NAME = "firstName";
	public static final String KEY_LAST_NAME = "lastName";

	private final boolean includePolicyViolations;
	private final boolean includePolicyOverrides;
	private final boolean includePolicyCleared;
	private final boolean includeVulnerabilities;

	public NotificationCountTransformer(final boolean includePolicyViolations, final boolean includePolivyOverrides,
			final boolean includePolicyCleared, final boolean includeVulnerabilities) {
		this.includePolicyViolations = includePolicyViolations;
		this.includePolicyOverrides = includePolivyOverrides;
		this.includePolicyCleared = includePolicyCleared;
		this.includeVulnerabilities = includeVulnerabilities;
	}

	public ProjectDigest transform(final ProjectAggregateData data) {
		final Map<String, String> map = new HashMap<>();
		final boolean displaySomeData = includePolicyViolations || includePolicyOverrides || includePolicyOverrides
				|| includeVulnerabilities;
		if (displaySomeData) {
			map.put(KEY_PROJECT_NAME, data.getProjectVersion().getProjectName());
			map.put(KEY_PROJECT_VERSION, data.getProjectVersion().getProjectVersionName());
			map.put(KEY_PROJECT_VERSION_LINK, data.getProjectVersion().getProjectVersionLink());
			map.put(KEY_PROJECT_ID, getProjectID(data.getProjectVersion()));
			map.put(KEY_PROJECT_VERSION_ID, getVersionID(data.getProjectVersion()));
			map.put(KEY_TOTAL_NOTIFICATION_COUNT, String.valueOf(data.getTotal()));
		}

		if (includePolicyViolations) {
			map.put(KEY_POLICY_VIOLATION_COUNT, String.valueOf(data.getPolicyViolationCount()));
		}
		if (includePolicyOverrides) {
			map.put(KEY_POLICY_OVERRIDE_COUNT, String.valueOf(data.getPolicyOverrideCount()));
		}
		if (includeVulnerabilities) {
			map.put(KEY_VULNERABILITY_COUNT, String.valueOf(data.getVulnerabilityCount()));
			map.put(KEY_VULN_ADDED_COUNT, String.valueOf(data.getVulnAddedCount()));
			map.put(KEY_VULN_UPDATED_COUNT, String.valueOf(data.getVulnUpdatedCount()));
			map.put(KEY_VULN_DELETED_COUNT, String.valueOf(data.getVulnDeletedCount()));
		}
		final FreemarkerTarget policyViolations = new FreemarkerTarget();
		final FreemarkerTarget policyOverrides = new FreemarkerTarget();
		final FreemarkerTarget vulnerabilities = new FreemarkerTarget();
		for (final ComponentAggregateData compData : data.getComponentList()) {
			final Map<String, String> compMap = new HashMap<>();
			if (displaySomeData) {
				compMap.put(KEY_COMPONENT_NAME, compData.getComponentName());
				compMap.put(KEY_COMPONENT_VERSION, compData.getComponentVersion());
				compMap.put(KEY_TOTAL_NOTIFICATION_COUNT, String.valueOf(compData.getTotal()));
			}
			if (includePolicyViolations) {
				compMap.put(KEY_POLICY_VIOLATION_COUNT, String.valueOf(compData.getPolicyViolationCount()));
			}
			if (includePolicyOverrides) {
				compMap.put(KEY_POLICY_OVERRIDE_COUNT, String.valueOf(compData.getPolicyOverrideCount()));
			}
			if (includeVulnerabilities) {
				compMap.put(KEY_VULNERABILITY_COUNT, String.valueOf(compData.getVulnerabilityCount()));
				compMap.put(KEY_VULN_ADDED_COUNT, String.valueOf(compData.getVulnAddedCount()));
				compMap.put(KEY_VULN_UPDATED_COUNT, String.valueOf(compData.getVulnUpdatedCount()));
				compMap.put(KEY_VULN_DELETED_COUNT, String.valueOf(compData.getVulnDeletedCount()));
			}
			policyViolations.addAll(createPolicyViolationData(compData.getPolicyViolationList()));
			policyOverrides.addAll(createPolicyOverrideData(compData.getPolicyOverrideList()));
			if (compData.getVulnerabilityCount() > 0) {
				vulnerabilities.addAll(createVulnerabilityData(compData.getVulnerabilitySummary()));
			}
		}
		return new ProjectDigest(map, policyViolations, policyOverrides, vulnerabilities);
	}

	private FreemarkerTarget createPolicyViolationData(final List<PolicyViolationContentItem> notifications) {
		final FreemarkerTarget templateData = new FreemarkerTarget();
		if (includePolicyViolations) {
			for (final PolicyViolationContentItem item : notifications) {
				for (final PolicyRule rule : item.getPolicyRuleList()) {
					final Map<String, String> itemMap = new HashMap<>();
					itemMap.put(KEY_COMPONENT_NAME, item.getComponentName());
					itemMap.put(KEY_COMPONENT_VERSION, item.getComponentVersion());
					itemMap.put(KEY_POLICY_NAME, rule.getName());
					templateData.add(itemMap);
				}
			}
		}
		return templateData;
	}

	private FreemarkerTarget createPolicyOverrideData(final List<PolicyOverrideContentItem> notifications) {
		final FreemarkerTarget templateData = new FreemarkerTarget();
		if (includePolicyOverrides) {
			for (final PolicyOverrideContentItem item : notifications) {
				for (final PolicyRule rule : item.getPolicyRuleList()) {
					final Map<String, String> itemMap = new HashMap<>();
					itemMap.put(KEY_COMPONENT_NAME, item.getComponentName());
					itemMap.put(KEY_COMPONENT_VERSION, item.getComponentVersion());
					itemMap.put(KEY_FIRST_NAME, item.getFirstName());
					itemMap.put(KEY_LAST_NAME, item.getLastName());
					itemMap.put(KEY_POLICY_NAME, rule.getName());
					templateData.add(itemMap);
				}
			}
		}
		return templateData;
	}

	private FreemarkerTarget createVulnerabilityData(final ComponentVulnerabilitySummary vulnSummary) {
		final FreemarkerTarget templateData = new FreemarkerTarget();
		if (includeVulnerabilities) {
			final Map<String, String> itemMap = new HashMap<>();
			itemMap.put(KEY_COMPONENT_NAME, vulnSummary.getComponentName());
			itemMap.put(KEY_COMPONENT_VERSION, vulnSummary.getComponentVersion());
			itemMap.put(KEY_VULN_HIGH_COUNT, String.valueOf(vulnSummary.getHighCount()));
			itemMap.put(KEY_VULN_MEDIUM_COUNT, String.valueOf(vulnSummary.getMediumCount()));
			itemMap.put(KEY_VULN_LOW_COUNT, String.valueOf(vulnSummary.getLowCount()));
			itemMap.put(KEY_VULN_TOTAL_COUNT, String.valueOf(vulnSummary.getTotalCount()));
			templateData.add(itemMap);
		}
		return templateData;
	}

	private String getProjectID(final ProjectVersion projectVersion) {
		try {
			return projectVersion.getProjectId().toString();
		} catch (final MissingUUIDException e) {
			return "";
		}
	}

	private String getVersionID(final ProjectVersion projectVersion) {
		try {
			return projectVersion.getVersionId().toString();
		} catch (final MissingUUIDException e) {
			return "";
		}
	}
}
