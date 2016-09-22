package com.blackducksoftware.integration.email.mock;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.notifier.routers.AbstractDigestRouter;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class TestDigestRouter extends AbstractDigestRouter {
	private final Logger logger = LoggerFactory.getLogger(TestDigestRouter.class);
	private final String lastRunPath;
	private final String initialStartDate;

	public TestDigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, userRestService, emailMessagingService);
		lastRunPath = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".lastrun.file");
		initialStartDate = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".start.date");
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
	public String getRouterPropertyKey() {
		return "digest";
	}
}
