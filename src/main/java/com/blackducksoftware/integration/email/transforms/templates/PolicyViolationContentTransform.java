package com.blackducksoftware.integration.email.transforms.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.EmailContentItem;
import com.blackducksoftware.integration.email.model.PolicyViolationContentItem;

public class PolicyViolationContentTransform extends AbstractPolicyContentTransform {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationContentTransform.class);

	@Override
	public List<Map<String, Object>> transform(final EmailContentItem item) {
		List<Map<String, Object>> templateData = new ArrayList<>();
		try {
			final PolicyViolationContentItem policyItem = (PolicyViolationContentItem) item;
			templateData = handleNotification(policyItem.getProjectName(), policyItem.getProjectVersion(),
					policyItem.getComponentName(), policyItem.getComponentVersion(), policyItem.getPolicyName());
		} catch (final Exception e) {
			logger.error("Error Transforming: " + item, e);
		}
		return templateData;
	}
}
