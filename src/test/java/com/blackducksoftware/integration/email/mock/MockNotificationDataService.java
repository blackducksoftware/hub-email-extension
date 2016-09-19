package com.blackducksoftware.integration.email.mock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyNotificationFilter;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class MockNotificationDataService extends NotificationDataService {

	public MockNotificationDataService(final RestConnection restConnection, final Gson gson,
			final JsonParser jsonParser, final PolicyNotificationFilter policyFilter) {
		super(restConnection, gson, jsonParser, policyFilter);
	}

	@Override
	public List<NotificationContentItem> getAllNotifications(final Date startDate, final Date endDate)
			throws IOException, URISyntaxException, BDRestException {
		return createNotificationList();
	}

	private List<NotificationContentItem> createNotificationList() {
		final List<NotificationContentItem> contentList = new ArrayList<>();
		contentList.addAll(createPolicyViolations());
		contentList.addAll(createPolicyOverrides());
		contentList.addAll(createVulnerabilities());
		contentList.addAll(createPolicyViolationsCleared());
		return contentList;
	}

	private List<PolicyViolationContentItem> createPolicyViolations() {
		final List<PolicyViolationContentItem> itemList = new ArrayList<>();
		for (int index = 0; index < 5; index++) {
			final ProjectVersion projectVersion = new ProjectVersion();
			final String componentName = "Component" + index;
			final String componentVersion = "Version" + index;
			final UUID componentId = UUID.randomUUID();
			final UUID componentVersionId = UUID.randomUUID();
			final List<PolicyRule> policyRuleList = new ArrayList<>();
			final PolicyViolationContentItem item = new PolicyViolationContentItem(projectVersion, componentName,
					componentVersion, componentId, componentVersionId, policyRuleList);
			itemList.add(item);
		}
		return itemList;

	}

	private List<PolicyOverrideContentItem> createPolicyOverrides() {
		final List<PolicyOverrideContentItem> itemList = new ArrayList<>();
		for (int index = 0; index < 5; index++) {
			final ProjectVersion projectVersion = new ProjectVersion();
			final String componentName = "Component" + index;
			final String componentVersion = "Version" + index;
			final UUID componentId = UUID.randomUUID();
			final UUID componentVersionId = UUID.randomUUID();
			final String firstName = "firstName";
			final String lastName = "lastName";
			final List<PolicyRule> policyRuleList = new ArrayList<>();
			final PolicyOverrideContentItem item = new PolicyOverrideContentItem(projectVersion, componentName,
					componentVersion, componentId, componentVersionId, policyRuleList, firstName, lastName);
			itemList.add(item);
		}
		return itemList;
	}

	private List<VulnerabilityContentItem> createVulnerabilities() {
		final List<VulnerabilityContentItem> itemList = new ArrayList<>();
		for (int index = 0; index < 5; index++) {
			final ProjectVersion projectVersion = new ProjectVersion();
			final String componentName = "Component" + index;
			final String componentVersion = "Version" + index;
			final UUID componentId = UUID.randomUUID();
			final UUID componentVersionId = UUID.randomUUID();
			final VulnerabilityContentItem item = new VulnerabilityContentItem(projectVersion, componentName,
					componentVersion, componentId, componentVersionId, createVulnSourceIds(), createVulnSourceIds(),
					createVulnSourceIds());
			itemList.add(item);
		}
		return itemList;
	}

	private List<VulnerabilitySourceQualifiedId> createVulnSourceIds() {
		final List<VulnerabilitySourceQualifiedId> sourceIdList = new ArrayList<>();
		for (int index = 0; index < 2; index++) {
			final VulnerabilitySourceQualifiedId sourceId = new VulnerabilitySourceQualifiedId("source", "id");
			sourceIdList.add(sourceId);
		}
		return sourceIdList;
	}

	private List<PolicyViolationClearedContentItem> createPolicyViolationsCleared() {
		final List<PolicyViolationClearedContentItem> itemList = new ArrayList<>();
		for (int index = 0; index < 5; index++) {
			final ProjectVersion projectVersion = new ProjectVersion();
			final String componentName = "Component" + index;
			final String componentVersion = "Version" + index;
			final UUID componentId = UUID.randomUUID();
			final UUID componentVersionId = UUID.randomUUID();
			final List<PolicyRule> policyRuleList = new ArrayList<>();
			final PolicyViolationClearedContentItem item = new PolicyViolationClearedContentItem(projectVersion,
					componentName, componentVersion, componentId, componentVersionId, policyRuleList);
			itemList.add(item);
		}
		return itemList;
	}
}
