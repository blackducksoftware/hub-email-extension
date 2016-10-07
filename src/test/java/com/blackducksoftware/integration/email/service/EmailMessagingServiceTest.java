package com.blackducksoftware.integration.email.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.EmailEngine;
import com.blackducksoftware.integration.email.mock.TestEmailEngine;
import com.blackducksoftware.integration.email.model.EmailTarget;
import com.blackducksoftware.integration.email.model.batch.CategoryData;
import com.blackducksoftware.integration.email.model.batch.ItemData;
import com.blackducksoftware.integration.email.model.batch.ProjectData;
import com.blackducksoftware.integration.email.notifier.AbstractDigestNotifier;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {

	private EmailEngine engine;

	@Before
	public void init() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("extension.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("ext.config.location", file.getCanonicalFile().getParent());
		engine = new TestEmailEngine();
		engine.start();
	}

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));
		final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "sampleTemplate.ftl", model);
		engine.getEmailMessagingService().sendEmailMessage(target);
	}

	@Test
	public void testDigest() throws Exception {
		final List<ProjectData> projectDataList = createProjectData();
		final Map<String, Object> model = new HashMap<>();
		model.put(AbstractDigestNotifier.KEY_START_DATE, String.valueOf(new Date()));
		model.put(AbstractDigestNotifier.KEY_END_DATE, String.valueOf(new Date()));
		model.put(AbstractDigestNotifier.KEY_USER_FIRST_NAME, "Hub");
		model.put(AbstractDigestNotifier.KEY_USER_LAST_NAME, "User");
		model.put(AbstractDigestNotifier.KEY_TOPICS_LIST, projectDataList);
		model.put("hubServerUrl", "http://hub-a.domain.com1/");
		model.put(AbstractDigestNotifier.KEY_NOTIFIER_CATEGORY, "Daily");

		final EmailTarget target = new EmailTarget("testUser@a.domain.com1", "digest.ftl", model);
		engine.getEmailMessagingService().sendEmailMessage(target);
	}

	private List<ProjectData> createProjectData() {
		final List<ProjectData> filteredList = new ArrayList<>();
		for (int index = 0; index < 5; index++) {
			final List<ItemData> itemList = new ArrayList<>(15);
			for (int itemIndex = 0; itemIndex < 15; itemIndex++) {
				final Map<String, String> dataMap = new HashMap<>();
				dataMap.put("KEY_" + itemIndex, "VALUE_" + itemIndex);
				itemList.add(new ItemData(dataMap));
			}
			final List<CategoryData> categoryMap = new ArrayList<>();
			for (int catIndex = 0; catIndex < 5; catIndex++) {
				final String category = "CATEGORY_" + catIndex;
				categoryMap.add(new CategoryData(category, itemList));
			}
			filteredList.add(new ProjectData("PROJECT_NAME>PROJECT_VERSION", categoryMap));
		}

		return filteredList;
	}
}
