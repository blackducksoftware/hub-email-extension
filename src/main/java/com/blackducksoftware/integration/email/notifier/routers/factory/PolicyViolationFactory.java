package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.PolicyViolationRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyViolationContentItem;

public class PolicyViolationFactory extends AbstractEmailFactory {
	public PolicyViolationFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final Map<String, AbstractContentTransform> transformMap) {
		super(emailMessagingService, customerProperties, transformMap);
	}

	@Override
	public AbstractEmailRouter<?> createInstance(final EmailTaskData data) {
		final PolicyViolationRouter router = new PolicyViolationRouter(getEmailMessagingService(),
				getCustomerProperties(), getTransformMap(), getTemplateName(), data);
		return router;
	}

	@Override
	public Set<String> getTemplateContentTypes() {
		final Set<String> topicSet = new HashSet<>();
		topicSet.add(PolicyViolationContentItem.class.getName());
		return topicSet;
	}

	@Override
	public String getTemplateName() {
		return "hubPolicyViolation.ftl";
	}
}
