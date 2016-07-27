package com.blackducksoftware.integration.email.notifier;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.email.messaging.ItemRouter;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.hub.notification.api.NotificationItem;

@Component
public abstract class AbstractEmailRouter
		extends ItemRouter<EmailSystemProperties, List<NotificationItem>, Map<String, Object>> {

}
