package com.blackducksoftware.integration.email.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.Application;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {
	private Application app;

	@Before
	public void init() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("test.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("customer.properties", file.getCanonicalPath());
		app = new Application();
	}

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		final List<String> recipients = Arrays.asList("psantos@blackducksoftware.com");
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));

		app.emailMessagingService.sendEmailMessage(app.customerProperties, recipients, model, "htmlTemplate.ftl");
	}
}
