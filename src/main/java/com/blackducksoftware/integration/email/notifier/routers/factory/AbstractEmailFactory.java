package com.blackducksoftware.integration.email.notifier.routers.factory;

import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;

public abstract class AbstractEmailFactory {
	public final static String TEMPLATE_DEFAULT = "htmlTemplate.ftl";
	private final EmailMessagingService emailMessagingService;
	private final CustomerProperties customerProperties;
	private final Map<String, AbstractContentTransform> transformMap;

	public AbstractEmailFactory(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final Map<String, AbstractContentTransform> transformMap) {
		this.emailMessagingService = emailMessagingService;
		this.customerProperties = customerProperties;
		this.transformMap = transformMap;
	}

	public EmailMessagingService getEmailMessagingService() {
		return emailMessagingService;
	}

	public CustomerProperties getCustomerProperties() {
		return customerProperties;
	}

	public Map<String, AbstractContentTransform> getTransformMap() {
		return transformMap;
	}

	public abstract String getTemplateName();

	public abstract Set<String> getTemplateContentTypes();

	public abstract AbstractEmailRouter<?> createInstance(EmailTaskData data);

}
