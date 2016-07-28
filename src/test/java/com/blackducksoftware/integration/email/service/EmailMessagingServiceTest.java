package com.blackducksoftware.integration.email.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.email.Application;

import freemarker.template.TemplateException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class EmailMessagingServiceTest {
	@Autowired
	private EmailMessagingService emailMessagingService;

	@Autowired
	private TemplateProcessor templateProcessor;

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));

		final String content = templateProcessor.getResolvedTemplate(model, "htmlTemplate.ftl");
		emailMessagingService.sendEmailMessage(
				Arrays.asList("ekerwin@blackducksoftware.com", "eric.kerwin@gmail.com", "akamen@blackducksoftware.com"),
				content);
	}

}
