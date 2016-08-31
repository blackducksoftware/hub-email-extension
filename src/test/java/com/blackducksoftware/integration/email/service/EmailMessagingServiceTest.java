package com.blackducksoftware.integration.email.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.mock.MockMailWrapper;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;
import com.blackducksoftware.integration.email.model.ProjectCategory;
import com.blackducksoftware.integration.email.model.ProjectsDigest;
import com.blackducksoftware.integration.email.model.VersionCategory;
import com.blackducksoftware.integration.email.notifier.EmailEngine;
import com.blackducksoftware.integration.email.notifier.routers.DigestRouter;
import com.blackducksoftware.integration.email.transformer.NotificationCountTransformer;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationCountData;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {

	private EmailEngine engine;
	private DigestRouter digestRouter;
	private JavaMailWrapper mockMailWrapper;
	private EmailMessagingService emailMessagingService;

	@Before
	public void init() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("test.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("customer.properties", file.getCanonicalPath());
		engine = new EmailEngine();
		// this code disables the default EmailMessagingService that is created
		// by the EmailEngine. Instead it creates a
		// router that uses a different email messaging service in order prevent
		// sending emails through java mail if you do not do this you may spam
		// emails to the users of the hub server configured in the
		// customer.properties file.
		mockMailWrapper = new MockMailWrapper(false);
		this.emailMessagingService = new EmailMessagingService(engine.customerProperties, engine.configuration,
				mockMailWrapper);
		digestRouter = new DigestRouter(engine.customerProperties, engine.notificationDataService,
				engine.userRestService, emailMessagingService);
		engine.routerManager.attachRouter(digestRouter);
	}

	@Test
	public void testRouter() throws Exception {
		digestRouter.run();
	}

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));
		final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "htmlTemplate.ftl", model);
		emailMessagingService.sendEmailMessage(target);
	}

	@Test
	public void testDigest() throws Exception {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName("Test Project");
		projectVersion.setProjectVersionName("0.1.0-TEST");
		final NotificationCountData countData = new NotificationCountData(new Date(), new Date(), projectVersion, 10, 3,
				2, 5, 1, 2, 3);
		final NotificationCountTransformer transformer = new NotificationCountTransformer();
		final Map<String, String> dataMap = transformer.transform(countData);
		final ProjectsDigest projectsDigest = new ProjectsDigest();
		final ProjectCategory projectCategory = new ProjectCategory(projectVersion.getProjectName(),
				new HashSet<VersionCategory>());
		final VersionCategory versionCategory = new VersionCategory(projectVersion.getProjectVersionName(), dataMap);
		projectCategory.getCategoryData().add(versionCategory);
		projectsDigest.add(projectCategory);

		final Map<String, Object> model = new HashMap<>();
		model.put("startDate", String.valueOf(countData.getStartDate()));
		model.put("endDate", String.valueOf(countData.getEndDate()));
		model.put("hubUserName", "Mr./Ms. Hub User");
		model.put("notificationCounts", projectsDigest);
		model.put("hubServerUrl", "http://eng-hub-valid03.dc1.lan/");

		final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "digest.ftl", model);
		emailMessagingService.sendEmailMessage(target);
	}
}
