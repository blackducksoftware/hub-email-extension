package com.blackducksoftware.integration.email.batch.processor.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;
import com.blackducksoftware.integration.email.batch.processor.NotificationEvent;
import com.blackducksoftware.integration.email.batch.processor.ItemTypeEnum;
import com.blackducksoftware.integration.email.batch.processor.ProcessingAction;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;

public class PolicyViolationClearedConverterTest {
	private static final String PREFIX_RULE = "Rule ";
	private static final String VERSION = "Version";
	private static final String COMPONENT = "Component";
	private static final String PROJECT_VERSION_NAME = "ProjectVersionName";
	private static final String PROJECT_NAME = "ProjectName";

	private PolicyViolationClearedContentItem createNotification() throws URISyntaxException {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(PROJECT_NAME);
		projectVersion.setProjectVersionName(PROJECT_VERSION_NAME);
		final String componentName = COMPONENT;
		final String componentVersion = VERSION;
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
				+ componentVersionId;
		final List<PolicyRule> policyRuleList = new ArrayList<>();
		policyRuleList.add(new PolicyRule(null, "Rule 1", "description", true, true, null, "then", "you", "now", "me"));
		policyRuleList.add(new PolicyRule(null, "Rule 2", "description", true, true, null, "then", "you", "now", "me"));
		final PolicyViolationClearedContentItem item = new PolicyViolationClearedContentItem(new Date(), projectVersion,
				componentName, componentVersion, componentVersionUrl, policyRuleList);
		return item;
	}

	@Test
	public void testConverter() throws Exception {
		final PolicyViolationClearedContentItem notification = createNotification();
		final PolicyViolationClearedConverter converter = new PolicyViolationClearedConverter();
		final List<NotificationEvent> eventList = converter.convert(notification);
		assertEquals(2, eventList.size());
		int index = 1;
		for (final NotificationEvent event : eventList) {
			assertEquals(ProcessingAction.REMOVE, event.getAction());
			assertEquals(PROJECT_NAME, event.getProjectName());
			assertEquals(PROJECT_VERSION_NAME, event.getProjectVersion());
			assertEquals(NotificationCategoryEnum.POLICY_VIOLATION, event.getCategoryType());
			assertTrue(event.getVulnerabilityIdSet().isEmpty());
			final Set<ItemEntry> dataSet = event.getDataSet();
			final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);

			assertTrue(dataSet.contains(componentKey));

			final ItemEntry versionKey = new ItemEntry("", VERSION);
			assertTrue(dataSet.contains(versionKey));

			final ItemEntry ruleKey = new ItemEntry(ItemTypeEnum.RULE.name(), PREFIX_RULE + index);
			assertTrue(dataSet.contains(ruleKey));
			index++;
		}
	}
}
