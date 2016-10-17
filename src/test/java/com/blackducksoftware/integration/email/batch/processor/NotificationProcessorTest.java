package com.blackducksoftware.integration.email.batch.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.api.vulnerabilities.SeverityEnum;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityRestService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

public class NotificationProcessorTest {
	private static final String LAST_NAME = "LastName";
	private static final String FIRST_NAME = "FirstName";
	private static final String PREFIX_RULE = "Rule ";
	private static final String VERSION = "Version";
	private static final String COMPONENT = "Component";
	private static final String VERSION2 = "Version2";
	private static final String COMPONENT2 = "Component2";
	private static final String PROJECT_VERSION_NAME = "ProjectVersionName";
	private static final String PROJECT_NAME = "ProjectName";
	private static final String PROJECT_VERSION_NAME2 = "ProjectVersionName2";
	private static final String PROJECT_NAME2 = "ProjectName2";

	private NotificationProcessor processor;

	@Before
	public void initTest() throws Exception {
		final DataServicesFactory dataServices = Mockito.mock(DataServicesFactory.class);
		final VulnerabilityRestService vulnRestService = Mockito.mock(VulnerabilityRestService.class);
		Mockito.when(vulnRestService.getVulnerability(Mockito.anyString())).thenAnswer(new Answer<VulnerabilityItem>() {

			@Override
			public VulnerabilityItem answer(final InvocationOnMock invocation) throws Throwable {
				final Object[] args = invocation.getArguments();
				final String vulnId = (String) args[0];
				SeverityEnum severity = SeverityEnum.UNKNOWN;
				if (vulnId.startsWith("high")) {
					severity = SeverityEnum.HIGH;
				} else if (vulnId.startsWith("medium")) {
					severity = SeverityEnum.MEDIUM;
				} else if (vulnId.startsWith("low")) {
					severity = SeverityEnum.LOW;
				}
				return createVulnerability(vulnId, severity);
			}

		});
		Mockito.when(dataServices.getVulnerabilityRestService()).thenReturn(vulnRestService);
		processor = new NotificationProcessor(dataServices);
	}

	private VulnerabilityItem createVulnerability(final String vulnId, final SeverityEnum severity) {
		return new VulnerabilityItem(null, vulnId, "A vulnerability", "today", "a minute ago", 10.0, 5.0, 1.0, "",
				severity.name(), "", "", "", "", "", "", vulnId);
	}

	private PolicyOverrideContentItem createPolicyOverride(final Date createdTime, final String projectName,
			final String projectVersionName, final String componentName, final String componentVersion)
			throws URISyntaxException {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(projectName);
		projectVersion.setProjectVersionName(projectVersionName);
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final String componentUrl = "http://localhost/api/components/" + componentId;
		final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
				+ componentVersionId;
		final List<PolicyRule> policyRuleList = new ArrayList<>();
		policyRuleList.add(new PolicyRule(null, "Rule 1", "description", true, true, null, "then", "you", "now", "me"));
		policyRuleList.add(new PolicyRule(null, "Rule 2", "description", true, true, null, "then", "you", "now", "me"));
		final PolicyOverrideContentItem item = new PolicyOverrideContentItem(createdTime, projectVersion, componentName,
				componentVersion, componentUrl, componentVersionUrl, policyRuleList, FIRST_NAME, LAST_NAME);
		return item;
	}

	private PolicyViolationClearedContentItem createPolicyCleared(final Date createdTime, final String projectName,
			final String projectVersionName, final String componentName, final String componentVersion)
			throws URISyntaxException {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(projectName);
		projectVersion.setProjectVersionName(projectVersionName);
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final String componentUrl = "http://localhost/api/components/" + componentId;
		final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
				+ componentVersionId;
		final List<PolicyRule> policyRuleList = new ArrayList<>();
		policyRuleList.add(new PolicyRule(null, "Rule 1", "description", true, true, null, "then", "you", "now", "me"));
		policyRuleList.add(new PolicyRule(null, "Rule 2", "description", true, true, null, "then", "you", "now", "me"));
		final PolicyViolationClearedContentItem item = new PolicyViolationClearedContentItem(createdTime,
				projectVersion, componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList);
		return item;
	}

	private PolicyViolationContentItem createPolicyViolation(final Date createdTime, final String projectName,
			final String projectVersionName, final String componentName, final String componentVersion)
			throws URISyntaxException {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(projectName);
		projectVersion.setProjectVersionName(projectVersionName);
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final String componentUrl = "http://localhost/api/components/" + componentId;
		final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
				+ componentVersionId;
		final List<PolicyRule> policyRuleList = new ArrayList<>();
		policyRuleList.add(new PolicyRule(null, "Rule 1", "description", true, true, null, "then", "you", "now", "me"));
		policyRuleList.add(new PolicyRule(null, "Rule 2", "description", true, true, null, "then", "you", "now", "me"));
		final PolicyViolationContentItem item = new PolicyViolationContentItem(createdTime, projectVersion,
				componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList);
		return item;
	}

	private VulnerabilityContentItem createVulnerability(final Date createdTime, final String projectName,
			final String projectVersionName, final String componentName, final String componentVersion,
			final List<VulnerabilitySourceQualifiedId> added, final List<VulnerabilitySourceQualifiedId> updated,
			final List<VulnerabilitySourceQualifiedId> deleted) throws URISyntaxException {

		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName(projectName);
		projectVersion.setProjectVersionName(projectVersionName);
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
				+ componentVersionId;
		final VulnerabilityContentItem item = new VulnerabilityContentItem(createdTime, projectVersion, componentName,
				componentVersion, componentVersionUrl, added, updated, deleted);
		return item;
	}

	private void assertPolicyDataValid(final Collection<ProjectData> projectList) {
		for (final ProjectData project : projectList) {
			assertEquals(PROJECT_NAME, project.getProjectName());
			assertEquals(PROJECT_VERSION_NAME, project.getProjectVersion());

			for (final CategoryData category : project.getCategoryMap().values()) {
				assertEquals(NotificationCategoryEnum.POLICY_VIOLATION.name(), category.getCategoryKey());
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));

					for (int index = 1; index <= 2; index++) {
						final ItemEntry ruleKey = new ItemEntry(ItemTypeEnum.RULE.name(), PREFIX_RULE + index);
						assertTrue(dataSet.contains(ruleKey));
					}
				}
			}
		}
	}

	@Test
	public void testPolicyViolationAdd() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		notifications.add(createPolicyViolation(new Date(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT, VERSION));
		final Collection<ProjectData> projectList = processor.process(notifications);

		assertPolicyDataValid(projectList);
	}

	@Test
	public void testPolicyViolationOverride() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		notifications.add(createPolicyOverride(new Date(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT, VERSION));
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertTrue(projectList.isEmpty());
	}

	@Test
	public void testPolicyViolationCleared() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		notifications.add(createPolicyCleared(new Date(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT, VERSION));
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertTrue(projectList.isEmpty());
	}

	@Test
	public void testPolicyViolationAndOverride() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		final PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyOverrideContentItem policyOverride = createPolicyOverride(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyOverride);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertTrue(projectList.isEmpty());
	}

	@Test
	public void testPolicyViolationAndCleared() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		final PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyViolationClearedContentItem policyCleared = createPolicyCleared(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyCleared);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertTrue(projectList.isEmpty());
	}

	@Test
	public void testPolicyViolationAndClearedAndViolated() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyViolationClearedContentItem policyCleared = createPolicyCleared(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyCleared);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertPolicyDataValid(projectList);
	}

	@Test
	public void testPolicyViolationAndOverrideAndViolated() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyOverrideContentItem policyCleared = createPolicyOverride(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyCleared);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertPolicyDataValid(projectList);
	}

	@Test
	public void testComplexPolicyOverride() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyOverrideContentItem policyOverride = createPolicyOverride(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyOverride);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyViolationClearedContentItem policyCleared = createPolicyCleared(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyCleared);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertPolicyDataValid(projectList);
	}

	@Test
	public void testVulnerabilityAdded() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final DateTime dateTime = new DateTime();
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, vulnerabilities, Collections.emptyList(),
				Collections.emptyList());
		notifications.add(vulnerability);
		final Collection<ProjectData> projectList = processor.process(notifications);

		for (final ProjectData projectData : projectList) {
			assertEquals(3, projectData.getCategoryMap().size());
			for (final CategoryData category : projectData.getCategoryMap().values()) {
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));
				}
			}
		}
	}

	@Test
	public void testVulnerabilityUpdated() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final DateTime dateTime = new DateTime();
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, Collections.emptyList(), vulnerabilities,
				Collections.emptyList());
		notifications.add(vulnerability);
		final Collection<ProjectData> projectList = processor.process(notifications);

		for (final ProjectData projectData : projectList) {
			assertEquals(3, projectData.getCategoryMap().size());
			for (final CategoryData category : projectData.getCategoryMap().values()) {
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));
				}
			}
		}
	}

	@Test
	public void testVulnerabilityDeleted() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final DateTime dateTime = new DateTime();
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, Collections.emptyList(), Collections.emptyList(),
				vulnerabilities);
		notifications.add(vulnerability);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertTrue(projectList.isEmpty());
	}

	@Test
	public void testVulnAddedAndDeleted() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		final List<VulnerabilitySourceQualifiedId> vulnerabilities = new LinkedList<>();
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		vulnerabilities.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final DateTime dateTime = new DateTime();
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, vulnerabilities, Collections.emptyList(), vulnerabilities);
		notifications.add(vulnerability);
		final Collection<ProjectData> projectList = processor.process(notifications);
		assertTrue(projectList.isEmpty());
	}

	@Test
	public void testComplexVulnerability() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();

		final List<VulnerabilitySourceQualifiedId> added = new LinkedList<>();
		added.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		added.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		added.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final List<VulnerabilitySourceQualifiedId> updated = new LinkedList<>();
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id2"));
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));

		final List<VulnerabilitySourceQualifiedId> deleted = new LinkedList<>();

		deleted.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));
		deleted.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		deleted.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));
		dateTime = dateTime.plusSeconds(1);
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, added, updated, deleted);
		notifications.add(vulnerability);

		final Collection<ProjectData> projectList = processor.process(notifications);
		assertFalse(projectList.isEmpty());
		final Map<String, Integer> categoryItemMap = new HashMap<>();
		for (final ProjectData projectData : projectList) {
			assertEquals(2, projectData.getCategoryMap().size());
			for (final CategoryData category : projectData.getCategoryMap().values()) {
				categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));
				}
			}
		}
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
	}

	@Test
	public void testComplexVulnerabilityMulti() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();

		final List<VulnerabilitySourceQualifiedId> added1 = new LinkedList<>();
		added1.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		added1.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		added1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final List<VulnerabilitySourceQualifiedId> updated1 = new LinkedList<>();
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id2"));
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));

		final List<VulnerabilitySourceQualifiedId> deleted1 = new LinkedList<>();

		deleted1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));
		deleted1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		deleted1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));
		dateTime = dateTime.plusSeconds(1);
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, added1, updated1, deleted1);
		notifications.add(vulnerability);

		final List<VulnerabilitySourceQualifiedId> added2 = new LinkedList<>();
		added1.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		added1.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		added1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final List<VulnerabilitySourceQualifiedId> updated2 = new LinkedList<>();
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id2"));
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		updated1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));

		final List<VulnerabilitySourceQualifiedId> deleted2 = new LinkedList<>();

		deleted1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));
		deleted1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		deleted1.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));
		dateTime = dateTime.plusSeconds(1);
		final VulnerabilityContentItem vulnerability2 = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, added2, updated2, deleted2);
		notifications.add(vulnerability2);

		final Collection<ProjectData> projectList = processor.process(notifications);
		assertFalse(projectList.isEmpty());
		final Map<String, Integer> categoryItemMap = new HashMap<>();
		for (final ProjectData projectData : projectList) {
			assertEquals(2, projectData.getCategoryMap().size());
			for (final CategoryData category : projectData.getCategoryMap().values()) {
				categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));
				}
			}
		}
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
	}

	// @Test
	public void testComplexAllTypes() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyOverrideContentItem policyOverride = createPolicyOverride(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyOverride);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyViolationClearedContentItem policyCleared = createPolicyCleared(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyCleared);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);

		final List<VulnerabilitySourceQualifiedId> added = new LinkedList<>();
		added.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		added.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id"));
		added.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));

		final List<VulnerabilitySourceQualifiedId> updated = new LinkedList<>();
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "high_vuln_id"));
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "medium_vuln_id2"));
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		updated.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));

		final List<VulnerabilitySourceQualifiedId> deleted = new LinkedList<>();

		deleted.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id"));
		deleted.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id2"));
		deleted.add(new VulnerabilitySourceQualifiedId("vuln_source", "low_vuln_id1"));
		dateTime = dateTime.plusSeconds(1);
		final VulnerabilityContentItem vulnerability = createVulnerability(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION, added, updated, deleted);
		notifications.add(vulnerability);

		final Collection<ProjectData> projectList = processor.process(notifications);
		assertFalse(projectList.isEmpty());
		final Map<String, Integer> categoryItemMap = new HashMap<>();
		for (final ProjectData projectData : projectList) {
			assertEquals(3, projectData.getCategoryMap().size());
			for (final CategoryData category : projectData.getCategoryMap().values()) {
				categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));
				}
			}
		}
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.POLICY_VIOLATION.name()).intValue());
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
		assertEquals(2, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
	}

	public void testMultiProjectPolicy() throws Exception {
		final SortedSet<NotificationContentItem> notifications = new TreeSet<>();
		DateTime dateTime = new DateTime();
		PolicyViolationContentItem policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT2,
				VERSION2);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME2, PROJECT_VERSION_NAME2, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME2, PROJECT_VERSION_NAME2, COMPONENT2,
				VERSION2);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		final PolicyOverrideContentItem policyOverride = createPolicyOverride(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT, VERSION);
		notifications.add(policyOverride);
		dateTime = dateTime.plusSeconds(1);
		policyViolation = createPolicyViolation(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT,
				VERSION);
		notifications.add(policyViolation);
		dateTime = dateTime.plusSeconds(1);
		PolicyViolationClearedContentItem policyCleared = createPolicyCleared(dateTime.toDate(), PROJECT_NAME,
				PROJECT_VERSION_NAME, COMPONENT2, VERSION);
		notifications.add(policyCleared);
		policyCleared = createPolicyCleared(dateTime.toDate(), PROJECT_NAME, PROJECT_VERSION_NAME, COMPONENT2,
				VERSION2);
		notifications.add(policyCleared);

		final Collection<ProjectData> projectList = processor.process(notifications);
		assertFalse(projectList.isEmpty());
		final Map<String, Integer> categoryItemMap = new HashMap<>();
		for (final ProjectData projectData : projectList) {
			assertEquals(3, projectData.getCategoryMap().size());
			for (final CategoryData category : projectData.getCategoryMap().values()) {
				categoryItemMap.put(category.getCategoryKey(), category.getItemList().size());
				for (final ItemData itemData : category.getItemList()) {
					final Set<ItemEntry> dataSet = itemData.getDataSet();
					final ItemEntry componentKey = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT);
					assertTrue(dataSet.contains(componentKey));

					final ItemEntry versionKey = new ItemEntry("", VERSION);
					assertTrue(dataSet.contains(versionKey));
					final ItemEntry componentKey2 = new ItemEntry(ItemTypeEnum.COMPONENT.name(), COMPONENT2);
					assertTrue(dataSet.contains(componentKey2));

					final ItemEntry versionKey2 = new ItemEntry("", VERSION2);
					assertTrue(dataSet.contains(versionKey2));
				}
			}
		}
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.POLICY_VIOLATION.name()).intValue());
		assertEquals(1, categoryItemMap.get(NotificationCategoryEnum.HIGH_VULNERABILITY.name()).intValue());
		assertEquals(2, categoryItemMap.get(NotificationCategoryEnum.MEDIUM_VULNERABILITY.name()).intValue());
	}
}
