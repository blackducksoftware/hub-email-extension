package com.blackducksoftware.integration.email.notifier.routers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.model.EmailData;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public abstract class AbstractEmailRouter<T extends NotificationItem>
		extends ItemRouter<EmailSystemProperties, List<T>, EmailData> {

	private final Logger logger = LoggerFactory.getLogger(AbstractEmailRouter.class);

	// @Autowired
	// private EmailMessagingService emailMessagingService;
	//
	// @Autowired
	// private TemplateProcessor templateProcessor;

	@Override
	public void receive(final List<T> data) {
		logger.info("Router received notification(s) count: " + (data == null ? 0 : data.size()));
		send(transform(data));
	}

	@Override
	public void send(final EmailData data) {
		// try {
		// final String content =
		// templateProcessor.getResolvedTemplate(data.getModel(),
		// "htmlTemplate.ftl");
		// emailMessagingService.sendEmailMessage(data.getAddresses(), content);
		// } catch (IOException | TemplateException | MessagingException e) {
		// logger.error("Error sending email..", e);
		// }
	}

	public abstract EmailData transform(List<T> data);

}
