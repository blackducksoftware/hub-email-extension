package com.blackducksoftware.integration.email.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.email.notifier.EmailEngine;

import freemarker.template.TemplateException;

public class EmailMessagingServiceTest {
	private static final List<String> recipients = Collections.emptyList();
	// private static final List<String> recipients =
	// Arrays.asList("ekerwin@blackducksoftware.com",
	// "akamen@blackducksoftware.com", "psantos@blackducksoftware.com",
	// "eric.kerwin@gmail.com");
	// private static final List<String> recipients =
	// Arrays.asList("ekerwin@blackducksoftware.com");
	// private static final List<String> recipients =
	// Arrays.asList("psantos@blackducksoftware.com");

	private EmailEngine engine;

	@Before
	public void init() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL propFileUrl = classLoader.getResource("test.properties");
		final File file = new File(propFileUrl.toURI());
		System.setProperty("customer.properties", file.getCanonicalPath());
		engine = new EmailEngine();
	}

	@Test
	public void testSendingEmail() throws IOException, MessagingException, TemplateException {
		final Map<String, Object> model = new HashMap<>();
		model.put("title", "A Glorious Day");
		model.put("message", "this should have html and plain text parts");
		model.put("items", Arrays.asList("apple", "orange", "pear", "banana"));

		engine.emailMessagingService.sendEmailMessage(engine.customerProperties, recipients, model, "htmlTemplate.ftl");
	}

	@Test
	public void testDigest() throws Exception {
		/**
		 * 'org.slf4j:slf4j-api:1.7.21'
		 *
		 * 'org.apache.logging.log4j:log4j-slf4j-impl:2.6.2'
		 *
		 * 'org.apache.logging.log4j:log4j-core:2.6.2'
		 *
		 * 'org.apache.commons:commons-lang3:3.4'
		 *
		 * 'commons-io:commons-io:2.5'
		 *
		 * 'commons-cli:commons-cli:1.3.1'
		 *
		 * 'com.google.code.gson:gson:2.7'
		 *
		 * 'org.jsoup:jsoup:1.9.2'
		 *
		 * 'javax.mail:mail:1.4.7'
		 */
		final List<Map<String, String>> policyViolations = new ArrayList<>();
		policyViolations.add(createPolicyViolation("James's Project", "0.5", "org.slf4j:slf4j-api", "1.7.21"));
		policyViolations.add(createPolicyViolation("James's Project", "0.5", "com.google.code.gson:gson", "2.7"));
		policyViolations
				.add(createPolicyViolation("James's Project", "0.5", "org.apache.commons:commons-lang3", "3.4"));
		policyViolations.add(createPolicyViolation("James's Project", "1.0.1", "javax.mail:mail", "1.4.7"));
		policyViolations.add(createPolicyViolation("James's Project", "1.0.1", "commons-cli:commons-cli", "1.3.1"));
		policyViolations.add(createPolicyViolation("Steve's Project", "2.3.5", "org.slf4j:slf4j-api", "1.7.21"));
		policyViolations.add(createPolicyViolation("Steve's Project", "2.3.5", "com.google.code.gson:gson", "2.7"));
		policyViolations.add(createPolicyViolation("Steve's Project", "2.3.5", "javax.mail:mail", "1.4.7"));

		final List<Map<String, String>> policyViolationOverrides = new ArrayList<>();
		policyViolationOverrides.add(createPolicyViolationOverride("James's Project", "0.5", "org.slf4j:slf4j-api",
				"1.7.21", "Paulo", "Santos"));
		policyViolationOverrides.add(createPolicyViolationOverride("James's Project", "0.5",
				"com.google.code.gson:gson", "2.7", "Giggles", "Kerwin"));
		policyViolationOverrides.add(createPolicyViolationOverride("James's Project", "0.5",
				"org.apache.commons:commons-lang3", "3.4", "Ari", "Kamen"));
		policyViolationOverrides.add(createPolicyViolationOverride("James's Project", "1.0.1", "javax.mail:mail",
				"1.4.7", "Charlie", "Brown"));
		policyViolationOverrides.add(createPolicyViolationOverride("James's Project", "1.0.1",
				"commons-cli:commons-cli", "1.3.1", "Richard", "Otte"));
		policyViolationOverrides.add(createPolicyViolationOverride("Steve's Project", "2.3.5", "org.slf4j:slf4j-api",
				"1.7.21", "Nick", "Rowles"));
		policyViolationOverrides.add(createPolicyViolationOverride("Steve's Project", "2.3.5",
				"com.google.code.gson:gson", "2.7", "Milton", "Friedman"));
		policyViolationOverrides.add(createPolicyViolationOverride("Steve's Project", "2.3.5", "javax.mail:mail",
				"1.4.7", "Murray", "Rothbard"));

		final List<Map<String, String>> policyViolationOverrideCancellations = new ArrayList<>();

		final List<Map<String, String>> securityVulnerabilities = new ArrayList<>();

		final Map<String, Object> model = new HashMap<>();
		model.put("hubUserName", "Mr./Ms. Hub User");
		model.put("policyViolations", policyViolations);
		model.put("policyViolationOverrides", policyViolationOverrides);
		model.put("policyViolationOverrideCancellations", policyViolationOverrideCancellations);
		model.put("securityVulnerabilities", securityVulnerabilities);
		model.put("hubServerUrl", "http://eng-hub-valid03.dc1.lan/");

		engine.emailMessagingService.sendEmailMessage(engine.customerProperties, recipients, model, "dailyDigest.ftl");
	}

	private Map<String, String> createPolicyViolation(final String projectName, final String projectVersionName,
			final String componentName, final String componentVersionName) {
		final Map<String, String> policyViolation = new HashMap<>();
		policyViolation.put("projectName", projectName);
		policyViolation.put("projectVersionName", projectVersionName);
		policyViolation.put("componentName", componentName);
		policyViolation.put("componentVersionName", componentVersionName);
		return policyViolation;
	}

	private Map<String, String> createPolicyViolationOverride(final String projectName, final String projectVersionName,
			final String componentName, final String componentVersionName, final String firstName,
			final String lastName) {
		final Map<String, String> policyViolationOverride = new HashMap<>();
		policyViolationOverride.put("projectName", projectName);
		policyViolationOverride.put("projectVersionName", projectVersionName);
		policyViolationOverride.put("componentName", componentName);
		policyViolationOverride.put("componentVersionName", componentVersionName);
		policyViolationOverride.put("firstName", firstName);
		policyViolationOverride.put("lastName", lastName);
		return policyViolationOverride;
	}

	private Map<String, String> createPolicyViolationOverrideCancellation() {
		final Map<String, String> policyViolationOverrideCancellation = new HashMap<>();
		return policyViolationOverrideCancellation;
	}

	private Map<String, String> createSecurityVulnerability(final String projectName, final String projectVersionName,
			final String componentName, final String componentVersionName) {
		final Map<String, String> securityVulnerability = new HashMap<>();
		securityVulnerability.put("projectName", projectName);
		securityVulnerability.put("projectVersionName", projectVersionName);
		securityVulnerability.put("componentName", componentName);
		securityVulnerability.put("componentVersionName", componentVersionName);
		return securityVulnerability;
	}

}
