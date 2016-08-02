package com.blackducksoftware.integration.email.notifier.routers;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.messaging.RouterTaskData;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

import freemarker.template.TemplateException;

@Component
public abstract class AbstractEmailRouter<T extends NotificationItem> extends ItemRouter<List<T>> {
	private final Logger logger = LoggerFactory.getLogger(AbstractEmailRouter.class);

	public final String KEY_PROJECT_NAME = "hub-project-name";
	public final String KEY_PROJECT_VERSION = "hub-project-version";
	public final String KEY_COMPONENT_NAME = "hub-component-name";
	public final String KEY_COMPONENT_VERSION = "hub-component-version";

	@Autowired
	private EmailMessagingService emailMessagingService;

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public void execute(final RouterTaskData<List<T>> taskData) {
		final List<T> data = taskData.getData();
		logger.info(
				"Router " + getName() + ": Received notification(s). Total count: " + (data == null ? 0 : data.size()));
		send(transform(data));
	}

	public void send(final EmailData data) {
		try {
			if (data != null && !data.getAddresses().isEmpty() && !data.getModel().isEmpty()) {
				emailMessagingService.sendEmailMessage(null, data.getAddresses(), data.getModel(), "htmlTemplate.ftl");
			} else {
				logger.info(
						"Router " + getName() + ": Address list empty or missing content.  No emails drafted to send.");
			}
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error("Error sending email..", e);
		}
	}

	public abstract EmailData transform(List<T> data);
}
