package com.blackducksoftware.integration.email.batch.processor;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;

public class NotificationEventTest {

	@Test
	public void testConstructor() {

		final ProcessingAction action = ProcessingAction.ADD;
		final String projectName = "project1";
		final String projectVersion = "projectVersion";
		final String componentName = "componentName";
		final String componentVersion = "componentVersion";
		final NotificationCategoryEnum categoryType = NotificationCategoryEnum.POLICY_VIOLATION;
		final Set<ItemEntry> dataSet = new HashSet<>();
		dataSet.add(new ItemEntry(ItemTypeEnum.COMPONENT.name(), "item"));
		dataSet.add(new ItemEntry(ItemTypeEnum.RULE.name(), "rule"));
		final Set<String> vulnerabilityIdSet = new HashSet<>();
		vulnerabilityIdSet.add("vuln_id1");
		vulnerabilityIdSet.add("vuln_id2");
		vulnerabilityIdSet.add("vuln_id1");
		final NotificationEvent event = new NotificationEvent(action, projectName, projectVersion, componentName,
				componentVersion, categoryType, dataSet, vulnerabilityIdSet);

		assertEquals(action, event.getAction());
		assertEquals(projectName, event.getProjectName());
		assertEquals(projectVersion, event.getProjectVersion());
		assertEquals(componentName, event.getComponentName());
		assertEquals(componentVersion, event.getComponentVersion());
		assertEquals(categoryType, event.getCategoryType());
		assertEquals(dataSet, event.getDataSet());
		assertEquals(2, event.getVulnerabilityIdSet().size());
		assertEquals(vulnerabilityIdSet, event.getVulnerabilityIdSet());
	}
}
