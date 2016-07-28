package com.blackducksoftware.integration.email.notifier;

import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.model.EmailMessage;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public abstract class AbstractEmailRouter<T extends NotificationItem>
		extends ItemRouter<EmailSystemConfiguration, List<T>, EmailMessage> {
}
