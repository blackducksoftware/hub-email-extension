package com.blackducksoftware.integration.email.transforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.api.component.ComponentVersionStatus;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.api.notification.PolicyOverrideNotificationItem;
import com.blackducksoftware.integration.hub.api.version.ReleaseItem;
import com.blackducksoftware.integration.hub.exception.NotificationServiceException;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class PolicyViolationOverrideTransform extends AbstractPolicyTransform {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationTransform.class);

	public PolicyViolationOverrideTransform(final NotificationService notificationService) {
		super(notificationService);
	}

	@Override
	public List<Map<String, Object>> transform(final NotificationItem item) {
		List<Map<String, Object>> templateData = new ArrayList<>();

		ReleaseItem releaseItem;
		try {
			final PolicyOverrideNotificationItem policyViolation = (PolicyOverrideNotificationItem) item;

			final String projectName = policyViolation.getContent().getProjectName();
			final List<ComponentVersionStatus> componentVersionList = new ArrayList<>();
			final ComponentVersionStatus componentStatus = new ComponentVersionStatus();
			componentStatus.setBomComponentVersionPolicyStatusLink(
					policyViolation.getContent().getBomComponentVersionPolicyStatusLink());
			componentStatus.setComponentName(policyViolation.getContent().getComponentName());
			componentStatus.setComponentVersionLink(policyViolation.getContent().getComponentVersionLink());

			componentVersionList.add(componentStatus);

			releaseItem = getNotificationService()
					.getProjectReleaseItemFromProjectReleaseUrl(policyViolation.getContent().getProjectVersionLink());
			templateData = handleNotification(projectName, componentVersionList, releaseItem);
			final Map<String, Object> templateMap = templateData.get(0);
			templateMap.put(KEY_FIRST_NAME, policyViolation.getContent().getFirstName());
			templateMap.put(KEY_LAST_NAME, policyViolation.getContent().getLastName());

		} catch (NotificationServiceException | UnexpectedHubResponseException e) {
			logger.error("Error Transforming: " + item, e);
		}
		return templateData;
	}
}
