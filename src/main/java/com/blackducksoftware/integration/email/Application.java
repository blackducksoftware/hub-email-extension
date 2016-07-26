package com.blackducksoftware.integration.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.google.gson.Gson;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

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
	public Configuration configuration() {
		final Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		// cfg.setDirectoryForTemplateLoading(new
		// File("/where/you/store/templates"));
		cfg.setClassLoaderForTemplateLoading(SpringApplication.class.getClassLoader(), "/");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setLogTemplateExceptions(false);

		return cfg;
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
