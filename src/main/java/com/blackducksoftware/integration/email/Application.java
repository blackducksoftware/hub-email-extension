package com.blackducksoftware.integration.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.email.service.ConfigurationResponseParser;
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

}
