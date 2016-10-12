package com.blackducksoftware.integration.email.batch.processor;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class NotificationEventTest {

	@Test
	public void testConstructor() {

		final ProcessingAction action = ProcessingAction.ADD;
		final String projectName = "project1";
		final String projectVersion = "projectVersion";
		final String componentName = "componentName";
		final String componentVersion = "componentVersion";
		final NotificationCategory categoryType = NotificationCategory.CATEGORY_POLICY_VIOLATON;
		final Map<String, String> dataMap = new HashMap<>();
		dataMap.put("item", NotificationItemType.ITEM_TYPE_COMPONENT.name());
		dataMap.put("rule", NotificationItemType.ITEM_TYPE_RULE.name());
		final Set<String> vulnerabilityIdSet = new HashSet<>();
		vulnerabilityIdSet.add("vuln_id1");
		vulnerabilityIdSet.add("vuln_id2");
		vulnerabilityIdSet.add("vuln_id1");
		final NotificationEvent event = new NotificationEvent(action, projectName, projectVersion, componentName,
				componentVersion, categoryType, dataMap, vulnerabilityIdSet);

		assertEquals(action, event.getAction());
		assertEquals(projectName, event.getProjectName());
		assertEquals(projectVersion, event.getProjectVersion());
		assertEquals(componentName, event.getComponentName());
		assertEquals(componentVersion, event.getComponentVersion());
		assertEquals(categoryType, event.getCategoryType());
		assertEquals(dataMap, event.getDataMap());
		assertEquals(2, event.getVulnerabilityIdSet().size());
		assertEquals(vulnerabilityIdSet, event.getVulnerabilityIdSet());
	}
}
