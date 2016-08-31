package com.blackducksoftware.integration.email.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.blackducksoftware.integration.email.model.FreemarkerTarget;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;

public class PolicyOverrideTransformerTest {

	@Test
	public void testPolicyOverrideTransform() {
		final String projectName = "Test Project";
		final String versionName = "0.1.0-TEST";
		final String componentName = "component";
		final String componentVersion = "componentVersion";
		final String policyRuleName = "aRule";
		final String firstName = "firstName";
		final String lastName = "lastName";
		final UUID componentID = UUID.randomUUID();
		final UUID componentVersionID = UUID.randomUUID();

		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(projectName);
		projectVersion.setProjectVersionName(versionName);
		final List<PolicyRule> policyRuleList = new ArrayList<>();
		final PolicyRule rule = new PolicyRule(null, policyRuleName, "", true, true, null, "", "", "", "");
		policyRuleList.add(rule);

		final PolicyOverrideContentItem item = new PolicyOverrideContentItem(projectVersion, componentName,
				componentVersion, componentID, componentVersionID, policyRuleList, firstName, lastName);
		final PolicyOverrideTransformer transformer = new PolicyOverrideTransformer();
		final FreemarkerTarget target = transformer.transform(item);

		assertNotNull(target);
		assertEquals(1, target.size());
		final Map<String, String> dataMap = target.get(0);
		assertEquals(projectName, dataMap.get(NotificationTransformer.KEY_PROJECT_NAME));
		assertEquals(versionName, dataMap.get(NotificationTransformer.KEY_PROJECT_VERSION));
		assertEquals(componentName, dataMap.get(NotificationTransformer.KEY_COMPONENT_NAME));
		assertEquals(componentVersion, dataMap.get(NotificationTransformer.KEY_COMPONENT_VERSION));
		assertEquals(policyRuleName, dataMap.get(NotificationTransformer.KEY_POLICY_NAME));
		assertEquals(firstName, dataMap.get(NotificationTransformer.KEY_FIRST_NAME));
		assertEquals(lastName, dataMap.get(NotificationTransformer.KEY_LAST_NAME));
	}
}
