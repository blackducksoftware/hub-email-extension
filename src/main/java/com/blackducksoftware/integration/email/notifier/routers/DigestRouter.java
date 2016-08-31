package com.blackducksoftware.integration.email.notifier.routers;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.ProjectCategory;
import com.blackducksoftware.integration.email.model.ProjectsDigest;
import com.blackducksoftware.integration.email.model.UserPreferences;
import com.blackducksoftware.integration.email.model.VersionCategory;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.email.transformer.NotificationCountTransformer;
import com.blackducksoftware.integration.hub.api.UserRestService;
import com.blackducksoftware.integration.hub.api.user.UserItem;
import com.blackducksoftware.integration.hub.dataservices.notification.NotificationDataService;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationCountData;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class DigestRouter extends AbstractRouter {
	private static final String LIST_NOTIFICATION_COUNTS = "notificationCounts";
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";

	private final Logger logger = LoggerFactory.getLogger(DigestRouter.class);
	private final long interval;
	private final String lastRunPath;
	private final String initialStartDate;

	public DigestRouter(final CustomerProperties customerProperties,
			final NotificationDataService notificationDataService, final UserRestService userRestService,
			final EmailMessagingService emailMessagingService) {
		super(customerProperties, notificationDataService, userRestService, emailMessagingService);
		interval = Long.valueOf(getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".interval.in.milliseconds"));
		lastRunPath = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".lastrun.file");
		initialStartDate = getCustomerProperties().getRouterVariableProperties()
				.get(getRouterPropertyKey() + ".start.date");
	}

	@Override
	public void run() {
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
			final List<NotificationCountData> notifications = getNotificationDataService()
					.getNotificationCounts(startDate, endDate);
			if (!notifications.isEmpty()) {
				final ProjectsDigest projectsDigest = createMap(notifications);
				final List<UserItem> users = getUserRestService().getAllUsers();
				final UserPreferences userPreferences = new UserPreferences(getCustomerProperties());
				for (final UserItem user : users) {
					try {
						final Map<String, Object> model = new HashMap<>();
						model.put(LIST_NOTIFICATION_COUNTS, projectsDigest);
						model.put(KEY_START_DATE, String.valueOf(startDate));
						model.put(KEY_END_DATE, String.valueOf(endDate));
						model.put("hubUserName", user.getUserName());
						final String emailAddress = user.getEmail();
						final String templateName = getTemplateName();
						final EmailTarget emailTarget = new EmailTarget(emailAddress, templateName, model);
						if (!userPreferences.isOptedOut(emailAddress, templateName)) {
							getEmailMessagingService().sendEmailMessage(emailTarget);
						}
					} catch (final Exception e) {
						logger.error("Error sending email to user", e);
					}
				}
			}
		} catch (final Exception e) {
			logger.error("Error sending the email", e);
		}
	}

	private ProjectsDigest createMap(final List<NotificationCountData> notifications) {
		final Map<String, ProjectCategory> projectsMap = new HashMap<>();
		final NotificationCountTransformer transformer = new NotificationCountTransformer();
		for (final NotificationCountData notification : notifications) {
			final String projectName = notification.getProjectVersion().getProjectName();
			final String projectVersion = notification.getProjectVersion().getProjectVersionName();
			final ProjectCategory projectCategory;
			if (projectsMap.containsKey(projectName)) {
				projectCategory = projectsMap.get(projectName);
			} else {
				projectCategory = new ProjectCategory(projectName, new HashSet<VersionCategory>());
				projectsMap.put(projectName, projectCategory);
			}
			// combination of project name and version should be unique and each
			// notification is for a specific project and version
			final VersionCategory versionCategory = new VersionCategory(projectVersion,
					transformer.transform(notification));
			projectCategory.getCategoryData().add(versionCategory);
		}

		final ProjectsDigest digest = new ProjectsDigest();
		for (final Map.Entry<String, ProjectCategory> entry : projectsMap.entrySet()) {
			digest.add(entry.getValue());
		}
		return digest;
	}

	@Override
	public String getTemplateName() {
		return "digest.ftl";
	}

	@Override
	public String getRouterPropertyKey() {
		return "digest";
	}

	@Override
	public long getIntervalMilliseconds() {
		return interval;
	}
}
