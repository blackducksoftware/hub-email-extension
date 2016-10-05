package com.blackducksoftware.integration.email.notifier;

import org.joda.time.DateTime;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public class WeeklyDigestNotifier extends AbstractDigestNotifier {

	public WeeklyDigestNotifier(final ExtensionProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, extensionConfigDataService, emailMessagingService);
	}

	@Override
	public DateRange createDateRange() {
		DateTime end = new DateTime();
		end = end.withHourOfDay(23);
		end = end.withMinuteOfHour(59);
		end = end.withSecondOfMinute(59);
		end = end.withMillisOfSecond(999);
		DateTime start = end.withTimeAtStartOfDay();
		start = end.minusDays(7);

		return new DateRange(start.toDate(), end.toDate());
	}

	@Override
	public String getNotifierPropertyKey() {
		return "weeklyDigest";
	}

	@Override
	public String getCategory() {
		return "Weekly";
	}
}
