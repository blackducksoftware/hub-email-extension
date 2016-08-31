package com.blackducksoftware.integration.email.transformer;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationCountData;

public class NotificationCountTransformerTest {

	@Test
	public void testTransform() {
		final String projectName = "Test Project";
		final String versionName = "0.1.0-TEST";
		final Date startDate = new Date();
		final int total = 10;
		final int policyViolationCount = 3;
		final int policyOverrideCount = 2;
		final int vulnCount = 5;
		final int vulnAdded = 1;
		final int vulnUpdated = 2;
		final int vulnDeleted = 3;
		final Date endDate = new Date();
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(projectName);
		projectVersion.setProjectVersionName(versionName);
		final NotificationCountData countData = new NotificationCountData(startDate, endDate, projectVersion, total,
				policyViolationCount, policyOverrideCount, vulnCount, vulnAdded, vulnUpdated, vulnDeleted);
		final NotificationCountTransformer transformer = new NotificationCountTransformer();
		final Map<String, String> dataMap = transformer.transform(countData);

		assertEquals(projectName, dataMap.get(NotificationCountTransformer.KEY_PROJECT_NAME));
		assertEquals(versionName, dataMap.get(NotificationCountTransformer.KEY_PROJECT_VERSION));
		assertEquals(String.valueOf(total), dataMap.get(NotificationCountTransformer.KEY_TOTAL_NOTIFICATION_COUNT));
		assertEquals(String.valueOf(policyViolationCount),
				dataMap.get(NotificationCountTransformer.KEY_POLICY_VIOLATION_COUNT));
		assertEquals(String.valueOf(policyOverrideCount),
				dataMap.get(NotificationCountTransformer.KEY_POLICY_OVERRIDE_COUNT));
		assertEquals(String.valueOf(vulnCount), dataMap.get(NotificationCountTransformer.KEY_VULNERABILITY_COUNT));
		assertEquals(String.valueOf(vulnAdded), dataMap.get(NotificationCountTransformer.KEY_VULN_ADDED_COUNT));
		assertEquals(String.valueOf(vulnUpdated), dataMap.get(NotificationCountTransformer.KEY_VULN_UPDATED_COUNT));
		assertEquals(String.valueOf(vulnDeleted), dataMap.get(NotificationCountTransformer.KEY_VULN_DELETED_COUNT));
	}
}
