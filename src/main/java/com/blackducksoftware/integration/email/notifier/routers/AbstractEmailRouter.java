package com.blackducksoftware.integration.email.notifier.routers;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.NotificationService;

import freemarker.template.TemplateException;

public abstract class AbstractEmailRouter<T> implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(AbstractEmailRouter.class);

	public final String KEY_PROJECT_NAME = "hub-project-name";
	public final String KEY_PROJECT_VERSION = "hub-project-version";
	public final String KEY_COMPONENT_NAME = "hub-component-name";
	public final String KEY_COMPONENT_VERSION = "hub-component-version";

	private final EmailMessagingService emailMessagingService;
	private final EmailTaskData taskData;
	private final CustomerProperties customerProperties;
	private final NotificationService notificationService;

	public AbstractEmailRouter(final EmailMessagingService emailMessagingService,
			final CustomerProperties customerProperties, final NotificationService notificationService,
			final EmailTaskData taskData) {
		this.emailMessagingService = emailMessagingService;
		this.taskData = taskData;
		this.customerProperties = customerProperties;
		this.notificationService = notificationService;
	}

	public String getName() {
		return getClass().getName();
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	@SuppressWarnings("unchecked")
	public void execute(final EmailTaskData taskData) {
		final List<T> data = (List<T>) taskData.getData();
		logger.info(
				"Router " + getName() + ": Received notification(s). Total count: " + (data == null ? 0 : data.size()));
		send(transform(data));
	}

	public void send(final EmailData data) {
		try {
			if (data != null && !data.getAddresses().isEmpty() && !data.getModel().isEmpty()) {
				emailMessagingService.sendEmailMessage(customerProperties, data.getAddresses(), data.getModel(),
						"htmlTemplate.ftl");
			} else {
				logger.info(
						"Router " + getName() + ": Address list empty or missing content.  No emails drafted to send.");
			}
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error("Error sending email..", e);
		}
	}

	public abstract EmailData transform(List<T> data);

	@Override
	public void run() {
		execute(taskData);
	}

}
