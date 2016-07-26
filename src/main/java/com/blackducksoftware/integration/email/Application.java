package com.blackducksoftware.integration.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.email.service.ConfigurationResponseParser;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;

@SpringBootApplication
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Gson gson() {
		return new Gson();
	}

	@Bean
	public ConfigurationResponseParser configurationResponseParser() {
		return new ConfigurationResponseParser();
	}

	@Bean
	public EmailMessagingService emailMessagingService() {
		return new EmailMessagingService();
	}

	@Bean
	public DateFormat notificationDateFormatter() {
		final DateFormat dateFormatter = new SimpleDateFormat(RestConnection.JSON_DATE_FORMAT);
		dateFormatter.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
		return dateFormatter;
	}

	@Bean
	public Date applicationStartDate() {
		return new Date();
	}
}
