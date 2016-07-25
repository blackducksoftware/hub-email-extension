package com.blackducksoftware.integration.email.service;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.email.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class ConfigurationResponseParserTest {
	@Autowired
	private ConfigurationResponseParser configurationResponseParser;

	@Test
	public void testConfigurationResponseIsPopulated() throws IOException {
	}

}
