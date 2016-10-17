package com.blackducksoftware.integration.email.notifier;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;

public class MonthlyDigestNotifier extends AbstractDigestNotifier {

	public MonthlyDigestNotifier(final ExtensionProperties customerProperties,
			final EmailMessagingService emailMessagingService, final DataServicesFactory dataservicesFactory) {
		super(customerProperties, emailMessagingService, dataservicesFactory);
	}

	@Override
	public DateRange createDateRange(final ZoneId zone) {
		final LocalDateTime end = LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59)
				.withNano(999);
		final LocalDateTime start = LocalDateTime.now().minusDays(1).minusMonths(1).withHour(0).withMinute(0)
				.withSecond(0).withNano(0);

		final ZonedDateTime endZonedTime = ZonedDateTime.of(end, zone);
		final ZonedDateTime startZonedTime = ZonedDateTime.of(start, zone);

		return new DateRange(Date.from(startZonedTime.toInstant()), Date.from(endZonedTime.toInstant()));
	}

	@Override
	public String getNotifierPropertyKey() {
		return "monthlyDigest";
	}

	@Override
	public String getCategory() {
		return "Monthly";
	}
}
