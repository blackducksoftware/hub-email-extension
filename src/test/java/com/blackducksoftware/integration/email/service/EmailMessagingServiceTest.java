package com.blackducksoftware.integration.email.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.email.Application;
import com.blackducksoftware.integration.email.model.EmailSystemConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class EmailMessagingServiceTest {
	@Autowired
	private ConfigurationResponseParser configurationResponseParser;

	@Autowired
	private EmailMessagingService emailMessagingService;

	@Test
	public void testSendingEmail() throws IOException {
		final String resourcePath = "defaultSystemConfig.json";
		final ClassPathResource configResponseResource = new ClassPathResource(resourcePath);
		final String json = IOUtils.toString(configResponseResource.getInputStream(), Charset.forName("UTF-8"));

		final EmailSystemConfiguration emailSystemConfiguration = configurationResponseParser.fromJson(json);
		assertTrue(emailSystemConfiguration.isOptIn());

		emailMessagingService.sendEmailMessage(emailSystemConfiguration);
	}
}
