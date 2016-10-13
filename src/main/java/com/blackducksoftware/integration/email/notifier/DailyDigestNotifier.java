package com.blackducksoftware.integration.email.notifier;

import org.joda.time.DateTime;

import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;

public class DailyDigestNotifier extends AbstractDigestNotifier {

	public DailyDigestNotifier(final ExtensionProperties customerProperties,
			final EmailMessagingService emailMessagingService, final DataServicesFactory dataservicesFactory) {
		super(customerProperties, emailMessagingService, dataservicesFactory);
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
	public String getNotifierPropertyKey() {
		return "dailyDigest";
	}

	@Override
	public String getCategory() {
		return "Daily";
	}
}
