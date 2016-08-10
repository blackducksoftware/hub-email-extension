package com.blackducksoftware.integration.email.transforms;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.notifier.routers.EmailContentItem;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.api.component.ComponentVersionStatus;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.api.notification.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.api.version.ReleaseItem;
import com.blackducksoftware.integration.hub.exception.NotificationServiceException;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
import com.blackducksoftware.integration.hub.notification.NotificationService;

public class PolicyViolationTransform extends AbstractPolicyTransform {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationTransform.class);

	public PolicyViolationTransform(final NotificationService notificationService) {
		super(notificationService);
	}

	@Override
	public List<EmailContentItem> transform(final NotificationItem item) {
		final List<EmailContentItem> templateData = new ArrayList<>();
		try {
			final RuleViolationNotificationItem policyViolation = (RuleViolationNotificationItem) item;
			final String projectName = policyViolation.getContent().getProjectName();
			final List<ComponentVersionStatus> componentVersionList = policyViolation.getContent()
					.getComponentVersionStatuses();
			ReleaseItem releaseItem;
			releaseItem = getNotificationService()
					.getProjectReleaseItemFromProjectReleaseUrl(policyViolation.getContent().getProjectVersionLink());
			handleNotification(projectName, componentVersionList, releaseItem, item, templateData);

		} catch (NotificationServiceException | UnexpectedHubResponseException e) {
			logger.error("Error Transforming: " + item, e);
		}
		return templateData;
	}

	@Override
	public void createContents(final String projectName, final String projectVersion, final String componentName,
			final String componentVersion, final String policyName, final NotificationItem item,
			final List<EmailContentItem> templateData) {
		templateData.add(new PolicyViolationContentItem(projectName, projectVersion, componentName, componentVersion,
				policyName));
	}
}
