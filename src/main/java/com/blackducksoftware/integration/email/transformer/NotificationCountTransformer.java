package com.blackducksoftware.integration.email.transformer;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.dataservices.notification.items.ProjectAggregateData;

public class NotificationCountTransformer {

	public static final String KEY_PROJECT_NAME = "projectName";
	public static final String KEY_PROJECT_VERSION = "projectVersionName";
	public static final String KEY_PROJECT_VERSION_LINK = "projectVersionLink";
	public static final String KEY_POLICY_VIOLATION_COUNT = "policyViolationCount";
	public static final String KEY_POLICY_OVERRIDE_COUNT = "policyOverrideCount";
	public static final String KEY_VULNERABILITY_COUNT = "vulnerabilityCount";
	public static final String KEY_TOTAL_NOTIFICATION_COUNT = "totalNotificationCount";
	public static final String KEY_VULN_ADDED_COUNT = "vulnAddedCount";
	public static final String KEY_VULN_UPDATED_COUNT = "vulnUpdatedCount";
	public static final String KEY_VULN_DELETED_COUNT = "vulnDeletedCount";

	public Map<String, String> transform(final ProjectAggregateData data) {
		final Map<String, String> map = new HashMap<>();
		map.put(KEY_PROJECT_NAME, data.getProjectVersion().getProjectName());
		map.put(KEY_PROJECT_VERSION, data.getProjectVersion().getProjectVersionName());
		map.put(KEY_PROJECT_VERSION_LINK, data.getProjectVersion().getProjectVersionLink());
		map.put(KEY_TOTAL_NOTIFICATION_COUNT, String.valueOf(data.getTotal()));
		map.put(KEY_POLICY_VIOLATION_COUNT, String.valueOf(data.getPolicyViolationCount()));
		map.put(KEY_POLICY_OVERRIDE_COUNT, String.valueOf(data.getPolicyOverrideCount()));
		map.put(KEY_VULNERABILITY_COUNT, String.valueOf(data.getVulnerabilityCount()));
		map.put(KEY_VULN_ADDED_COUNT, String.valueOf(data.getVulnAddedCount()));
		map.put(KEY_VULN_UPDATED_COUNT, String.valueOf(data.getVulnUpdatedCount()));
		map.put(KEY_VULN_DELETED_COUNT, String.valueOf(data.getVulnDeletedCount()));
		return map;
	}
}
