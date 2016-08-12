package com.blackducksoftware.integration.email.transforms.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.EmailContentItem;
import com.blackducksoftware.integration.email.model.PolicyOverrideContentItem;

public class PolicyOverrideContentTransform extends AbstractPolicyContentTransform {

	private final Logger logger = LoggerFactory.getLogger(PolicyViolationContentTransform.class);

	@Override
	public List<Map<String, Object>> transform(final EmailContentItem item) {
		List<Map<String, Object>> templateData = new ArrayList<>();

		try {
			final PolicyOverrideContentItem policyItem = (PolicyOverrideContentItem) item;
			templateData = handleNotification(policyItem.getProjectName(), policyItem.getProjectVersion(),
					policyItem.getComponentName(), policyItem.getComponentVersion(), policyItem.getPolicyName());
			final Map<String, Object> templateMap = templateData.get(0);
			templateMap.put(KEY_FIRST_NAME, policyItem.getFirstName());
			templateMap.put(KEY_LAST_NAME, policyItem.getLastName());

		} catch (final Exception e) {
			logger.error("Error Transforming", e);
		}
		return templateData;
	}

	@Override
	public String getContentItemType() {
		return PolicyOverrideContentItem.class.getName();
	}
}
