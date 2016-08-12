package com.blackducksoftware.integration.email.notifier.routers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transforms.templates.AbstractContentTransform;
import com.blackducksoftware.integration.hub.notification.NotificationService;

import freemarker.template.TemplateException;

public abstract class AbstractEmailRouter<T> implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(AbstractEmailRouter.class);

	public final String KEY_USER = "hubUserName";
	public final String KEY_HUB_URL = "hubServerUrl";

	private final EmailMessagingService emailMessagingService;
	private final EmailTaskData taskData;
	private final CustomerProperties customerProperties;
	private final NotificationService notificationService;
	private final Map<String, AbstractContentTransform> transformMap;
	private final String templateName;

	public AbstractEmailRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final Map<String, AbstractContentTransform> transformMap, final String templateName,
			final EmailTaskData taskData) {
		this.emailMessagingService = emailMessagingService;
		this.taskData = taskData;
		this.customerProperties = customerProperties;
		this.notificationService = notificationService;
		this.transformMap = transformMap;
		this.templateName = templateName;
	}

	public String getName() {
		return getClass().getName();
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public Map<String, AbstractContentTransform> getTransformMap() {
		return transformMap;
	}

	@SuppressWarnings("unchecked")
	public void execute(final EmailTaskData taskData) {
		final List<T> data = (List<T>) taskData.getData();
		logger.info(
				"Router " + getName() + ": Received notification(s). Total count: " + (data == null ? 0 : data.size()));

		if (!data.isEmpty()) {
			send(transform(data));
		}
	}

	public void send(final EmailData data) {
		try {
			if (data != null && !data.getAddresses().isEmpty() && !data.getModel().isEmpty()) {
				emailMessagingService.sendEmailMessage(customerProperties, data.getAddresses(), data.getModel(),
						getTemplateName());
			} else {
				logger.info(
						"Router " + getName() + ": Address list empty or missing content.  No emails drafted to send.");
			}
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error("Error sending email..", e);
		}
	}

	public String getTemplateName() {
		return templateName;
	}

	public abstract EmailData transform(List<T> data);

	@Override
	public void run() {
		execute(taskData);
	}

}
