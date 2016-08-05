package com.blackducksoftware.integration.email.transforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.component.api.ComponentVersionStatus;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
import com.blackducksoftware.integration.hub.notification.NotificationService;
import com.blackducksoftware.integration.hub.notification.NotificationServiceException;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;
import com.blackducksoftware.integration.hub.notification.api.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.version.api.ReleaseItem;

public class PolicyViolationOverrideTransform extends AbstractPolicyTransform {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationTransform.class);

	public PolicyViolationOverrideTransform(final NotificationService notificationService) {
		super(notificationService);
	}

	@Override
	public List<Map<String, Object>> transform(final NotificationItem item) {
		List<Map<String, Object>> templateData = new ArrayList<>();

		final RuleViolationNotificationItem policyViolation = (RuleViolationNotificationItem) item;
		final String projectName = policyViolation.getContent().getProjectName();
		final List<ComponentVersionStatus> componentVersionList = policyViolation.getContent()
				.getComponentVersionStatuses();
		ReleaseItem releaseItem;
		try {
			releaseItem = getNotificationService()
					.getProjectReleaseItemFromProjectReleaseUrl(policyViolation.getContent().getProjectVersionLink());
			templateData = handleNotification(projectName, componentVersionList, releaseItem);
		} catch (NotificationServiceException | UnexpectedHubResponseException e) {
			logger.error("Error Transforming: " + item, e);
		}
		return templateData;
	}
}
