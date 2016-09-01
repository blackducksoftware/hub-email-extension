package com.blackducksoftware.integration.email.transformer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ProjectAggregateData;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

public class NotificationCountTransformerTest {

	@Test
	public void testTransform() {
		final String projectName = "Test Project";
		final String versionName = "0.1.0-TEST";
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName("Test Project");
		projectVersion.setProjectVersionName("0.1.0-TEST");
		projectVersion.setProjectVersionLink("versionLink");
		final String componentName = "componentName";
		final String componentVersion = "componentVersion";
		final String firstName = "firstName";
		final String lastName = "lastName";
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final List<PolicyViolationContentItem> violationList = new ArrayList<>();
		final List<PolicyOverrideContentItem> overrideList = new ArrayList<>();
		final List<VulnerabilityContentItem> vulnerabilityList = new ArrayList<>();
		final PolicyRule rule = new PolicyRule(null, "aRule", "", true, true, null, "", "", "", "");
		final List<PolicyRule> ruleList = new ArrayList<>();
		ruleList.add(rule);
		final PolicyViolationContentItem violationContent = new PolicyViolationContentItem(projectVersion,
				componentName, componentVersion, componentId, componentVersionId, ruleList);
		final PolicyOverrideContentItem overrideContent = new PolicyOverrideContentItem(projectVersion, componentName,
				componentVersion, componentId, componentVersionId, ruleList, firstName, lastName);

		final List<VulnerabilitySourceQualifiedId> sourceIdList = new ArrayList<>();
		sourceIdList.add(new VulnerabilitySourceQualifiedId("source", "id"));
		final VulnerabilityContentItem vulnerabilityContent = new VulnerabilityContentItem(projectVersion,
				componentName, componentVersion, componentId, componentVersionId, sourceIdList, sourceIdList,
				sourceIdList);
		violationList.add(violationContent);
		overrideList.add(overrideContent);
		vulnerabilityList.add(vulnerabilityContent);
		final int sourceIDSize = sourceIdList.size();
		final ProjectAggregateData countData = new ProjectAggregateData(new Date(), new Date(), projectVersion,
				violationList, overrideList, vulnerabilityList, sourceIDSize, sourceIDSize, sourceIDSize);
		final NotificationCountTransformer transformer = new NotificationCountTransformer();
		final Map<String, String> dataMap = transformer.transform(countData);

		assertEquals(projectName, dataMap.get(NotificationCountTransformer.KEY_PROJECT_NAME));
		assertEquals(versionName, dataMap.get(NotificationCountTransformer.KEY_PROJECT_VERSION));
		assertEquals(projectVersion.getProjectVersionLink(),
				dataMap.get(NotificationCountTransformer.KEY_PROJECT_VERSION_LINK));
		assertEquals(String.valueOf(countData.getTotal()),
				dataMap.get(NotificationCountTransformer.KEY_TOTAL_NOTIFICATION_COUNT));
		assertEquals(String.valueOf(countData.getPolicyViolationCount()),
				dataMap.get(NotificationCountTransformer.KEY_POLICY_VIOLATION_COUNT));
		assertEquals(String.valueOf(countData.getPolicyOverrideCount()),
				dataMap.get(NotificationCountTransformer.KEY_POLICY_OVERRIDE_COUNT));
		assertEquals(String.valueOf(countData.getVulnerabilityCount()),
				dataMap.get(NotificationCountTransformer.KEY_VULNERABILITY_COUNT));
		assertEquals(String.valueOf(countData.getVulnAddedCount()),
				dataMap.get(NotificationCountTransformer.KEY_VULN_ADDED_COUNT));
		assertEquals(String.valueOf(countData.getVulnUpdatedCount()),
				dataMap.get(NotificationCountTransformer.KEY_VULN_UPDATED_COUNT));
		assertEquals(String.valueOf(countData.getVulnDeletedCount()),
				dataMap.get(NotificationCountTransformer.KEY_VULN_DELETED_COUNT));
	}
}
