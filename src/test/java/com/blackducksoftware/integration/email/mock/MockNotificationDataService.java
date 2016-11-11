package com.blackducksoftware.integration.email.mock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
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
        super(new MockLogger(), restConnection, gson, jsonParser, policyFilter);
    }

    @Override
    public SortedSet<NotificationContentItem> getAllNotifications(final Date startDate, final Date endDate)
            throws IOException, URISyntaxException, BDRestException {
        return createNotificationList();
    }

    private SortedSet<NotificationContentItem> createNotificationList() throws URISyntaxException {
        final SortedSet<NotificationContentItem> contentList = new TreeSet<>();
        contentList.addAll(createPolicyViolations());
        contentList.addAll(createPolicyOverrides());
        contentList.addAll(createVulnerabilities());
        contentList.addAll(createPolicyViolationsCleared());
        return contentList;
    }

    private List<PolicyViolationContentItem> createPolicyViolations() throws URISyntaxException {
        final List<PolicyViolationContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentUrl = "http://localhost/api/components/" + componentId;
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final List<PolicyRule> policyRuleList = new ArrayList<>();
            final PolicyViolationContentItem item = new PolicyViolationContentItem(new Date(), projectVersion,
                    componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList);
            itemList.add(item);
        }
        return itemList;

    }

    private List<PolicyOverrideContentItem> createPolicyOverrides() throws URISyntaxException {
        final List<PolicyOverrideContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentUrl = "http://localhost/api/components/" + componentId;
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final String firstName = "firstName";
            final String lastName = "lastName";
            final List<PolicyRule> policyRuleList = new ArrayList<>();
            final PolicyOverrideContentItem item = new PolicyOverrideContentItem(new Date(), projectVersion,
                    componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList, firstName,
                    lastName);
            itemList.add(item);
        }
        return itemList;
    }

    private List<VulnerabilityContentItem> createVulnerabilities() throws URISyntaxException {
        final List<VulnerabilityContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final VulnerabilityContentItem item = new VulnerabilityContentItem(new Date(), projectVersion,
                    componentName, componentVersion, componentVersionUrl, createVulnSourceIds(), createVulnSourceIds(),
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

    private List<PolicyViolationClearedContentItem> createPolicyViolationsCleared() throws URISyntaxException {
        final List<PolicyViolationClearedContentItem> itemList = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            final ProjectVersion projectVersion = new ProjectVersion();
            final String componentName = "Component" + index;
            final String componentVersion = "Version" + index;
            final UUID componentId = UUID.randomUUID();
            final UUID componentVersionId = UUID.randomUUID();
            final String componentUrl = "http://localhost/api/components/" + componentId;
            final String componentVersionUrl = "http://localhost/api/components/" + componentId + "/versions/"
                    + componentVersionId;
            final List<PolicyRule> policyRuleList = new ArrayList<>();
            final PolicyViolationClearedContentItem item = new PolicyViolationClearedContentItem(new Date(),
                    projectVersion, componentName, componentVersion, componentUrl, componentVersionUrl, policyRuleList);
            itemList.add(item);
        }
        return itemList;
    }
}
