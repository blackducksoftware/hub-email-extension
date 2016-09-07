package com.blackducksoftware.integration.email.transformer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.blackducksoftware.integration.email.model.ProjectDigest;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ComponentAggregateData;
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
		final int total = violationList.size() + overrideList.size() + vulnerabilityList.size();
		final ComponentAggregateData componentData = new ComponentAggregateData(componentName, componentVersion,
				violationList, overrideList, vulnerabilityList, sourceIDSize, sourceIDSize, sourceIDSize);
		final List<ComponentAggregateData> componentList = new ArrayList<>();
		componentList.add(componentData);
		final ProjectAggregateData countData = new ProjectAggregateData(new Date(), new Date(), projectVersion,
				violationList.size(), overrideList.size(), vulnerabilityList.size(), total, sourceIDSize, sourceIDSize,
				sourceIDSize, componentList);
		final NotificationCountTransformer transformer = new NotificationCountTransformer();
		final ProjectDigest digest = transformer.transform(countData);

		assertEquals(projectName, digest.getProjectData().get(NotificationCountTransformer.KEY_PROJECT_NAME));
		assertEquals(versionName, digest.getProjectData().get(NotificationCountTransformer.KEY_PROJECT_VERSION));
		assertEquals(projectVersion.getProjectVersionLink(),
				digest.getProjectData().get(NotificationCountTransformer.KEY_PROJECT_VERSION_LINK));
		assertEquals(String.valueOf(countData.getTotal()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_TOTAL_NOTIFICATION_COUNT));
		assertEquals(String.valueOf(countData.getPolicyViolationCount()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_POLICY_VIOLATION_COUNT));
		assertEquals(String.valueOf(countData.getPolicyOverrideCount()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_POLICY_OVERRIDE_COUNT));
		assertEquals(String.valueOf(countData.getVulnerabilityCount()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_VULNERABILITY_COUNT));
		assertEquals(String.valueOf(countData.getVulnAddedCount()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_VULN_ADDED_COUNT));
		assertEquals(String.valueOf(countData.getVulnUpdatedCount()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_VULN_UPDATED_COUNT));
		assertEquals(String.valueOf(countData.getVulnDeletedCount()),
				digest.getProjectData().get(NotificationCountTransformer.KEY_VULN_DELETED_COUNT));
	}
}
