package com.blackducksoftware.integration.email.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;

import org.junit.Test;

import com.blackducksoftware.integration.email.model.CustomerProperties;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {

	private EmailMessagingService emailMessagingService;

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		System.setProperty("customer.properties",
				"/Users/psantos/git/hub-extensions/email-notifications/hub-email-extension/src/main/resources/application-default.properties");
		final CustomerProperties customerProperties = new CustomerProperties(new Properties());
		final List<String> recipients = Arrays.asList("ekerwin@blackducksoftware.com", "eric.kerwin@gmail.com",
				"akamen@blackducksoftware.com");
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));

		// emailMessagingService.sendEmailMessage(customerProperties,
		// recipients, model, "htmlTemplate.ftl");
	}

}
