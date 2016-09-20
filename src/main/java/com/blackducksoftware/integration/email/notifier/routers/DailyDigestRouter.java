package com.blackducksoftware.integration.email.notifier.routers;

import org.joda.time.DateTime;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;

public class DailyDigestRouter extends DigestRouter {

	public DailyDigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, userRestService, emailMessagingService);
	}

	@Override
	public DateRange createDateRange() {
		DateTime end = new DateTime().minusDays(1);
		end = end.withHourOfDay(23);
		end = end.withMinuteOfHour(59);
		end = end.withSecondOfMinute(59);
		end = end.withMillisOfSecond(999);
		final DateTime start = end.withTimeAtStartOfDay();

		return new DateRange(start.toDate(), end.toDate());
	}

	@Override
	public String getRouterPropertyKey() {
		return "dailyDigest";
	}
}
