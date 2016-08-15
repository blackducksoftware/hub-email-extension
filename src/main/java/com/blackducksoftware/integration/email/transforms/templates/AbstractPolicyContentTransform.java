package com.blackducksoftware.integration.email.transforms.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractPolicyContentTransform extends AbstractContentTransform {
	public final static String KEY_POLICY_NAME = "policyName";
	public final static String KEY_FIRST_NAME = "firstName";
	public final static String KEY_LAST_NAME = "lastName";

	public List<Map<String, Object>> handleNotification(final String projectName, final String projectVersion,
			final String componentName, final String componentVersion, final List<String> policyNames) {
		final List<Map<String, Object>> templateData = new ArrayList<>();

		final Map<String, Object> itemMap = new HashMap<>();
		itemMap.put(KEY_PROJECT_NAME, projectName);
		itemMap.put(KEY_PROJECT_VERSION, projectVersion);
		itemMap.put(KEY_COMPONENT_NAME, componentName);
		itemMap.put(KEY_COMPONENT_VERSION, componentVersion);
		itemMap.put(KEY_POLICY_NAME, StringUtils.join(policyNames, ", "));
		templateData.add(itemMap);

		return templateData;
	}
}
