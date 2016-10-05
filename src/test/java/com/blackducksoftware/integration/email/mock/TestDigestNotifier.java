package com.blackducksoftware.integration.email.mock;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.notifier.AbstractDigestNotifier;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class TestDigestNotifier extends AbstractDigestNotifier {
	private final Logger logger = LoggerFactory.getLogger(TestDigestNotifier.class);
	private final String lastRunPath;
	private final String initialStartDate;

	public TestDigestNotifier(final ExtensionProperties customerProperties,
			final NotificationDataService notificationDataService,
			final ExtensionConfigDataService extensionConfigDataService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, extensionConfigDataService, emailMessagingService);
		lastRunPath = getCustomerProperties().getNotifierVariableProperties()
				.get(getNotifierPropertyKey() + ".lastrun.file");
		initialStartDate = getCustomerProperties().getNotifierVariableProperties()
				.get(getNotifierPropertyKey() + ".start.date");
	}

	@Override
	public DateRange createDateRange() {
		try {
			Date startDate = null;
			final File lastRunFile = new File(lastRunPath);
			if (lastRunFile.exists()) {
				final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
				startDate = RestConnection.parseDateString(lastRunValue);
				startDate = new Date(startDate.getTime() + 1);
			} else {
				final String lastRunValue = initialStartDate;
				startDate = RestConnection.parseDateString(lastRunValue);
			}

			final Date endDate = new Date();
			FileUtils.write(lastRunFile, RestConnection.formatDate(endDate), "UTF-8");
			return new DateRange(startDate, endDate);
		} catch (final Exception e) {
			logger.error("Error creating date range", e);
			final Date date = new Date();
			return new DateRange(date, date);
		}
	}

	@Override
	public String getNotifierPropertyKey() {
		return "digest";
	}

	@Override
	public String getCategory() {
		return "daily";
	}
}
