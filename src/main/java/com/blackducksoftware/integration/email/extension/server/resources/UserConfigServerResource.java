package com.blackducksoftware.integration.email.extension.server.resources;

import java.util.Collection;
import java.util.Collections;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.blackducksoftware.integration.email.extension.server.api.model.ConfigOption;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserConfigServerResource extends ServerResource {

	@Get("json")
	public String represent() {
		final JsonObject json = new JsonObject();

		json.addProperty("totalCount", 4);

		final JsonArray items = new JsonArray();

		items.add(getConfigElement("optIn", "BOOLEAN", "Opt In / Opt Out", true, true,
				"Whether or not emails should be triggered from published notifications", Collections.EMPTY_LIST,
				Lists.newArrayList("false")));

		final Collection<ConfigOption> emailFreqOptions = Lists.newArrayList();
		emailFreqOptions.add(new ConfigOption("daily", "Daily"));
		emailFreqOptions.add(new ConfigOption("weekly", "Weekly"));
		emailFreqOptions.add(new ConfigOption("monthly", "Monthly"));

		items.add(getConfigElement("emailFrequency", "STRING", "Email Frequency", false, true,
				"How often the Hub should be checked for notifications that might trigger emails", emailFreqOptions,
				Lists.newArrayList("weekly")));

		items.add(getConfigElement("templateName", "STRING", "Template Name", false, true,
				"The template for e-mail rendering", Collections.EMPTY_LIST, Lists.newArrayList("")));

		final Collection<ConfigOption> emailTriggerOptions = Lists.newArrayList();
		emailTriggerOptions.add(new ConfigOption("policyViolation", "Policy Violation"));
		emailTriggerOptions
				.add(new ConfigOption("policyViolationOverrideCancellation", "Policy Violation Override Cancellation"));
		emailTriggerOptions.add(new ConfigOption("overridePolicyViolation", "Override Policy Violation"));
		emailTriggerOptions.add(new ConfigOption("securityVulnerabilities", "Security Vulnerabilities"));

		items.add(getConfigElement("emailTriggers", "ARRAY", "Reasons to Trigger Emails", false, false,
				"The user can specify any number of reasons that will trigger an email", emailTriggerOptions,
				Lists.newArrayList("policyViolation", "securityVulnerabilities")));

		json.add("items", items);

		return json.toString();
	}

	private JsonElement getConfigElement(final String name, final String optionType, final String title,
			final boolean required, final boolean singleValue, final String description,
			final Collection<ConfigOption> configOptions, final Collection<String> defaultValues) {
		final JsonObject json = new JsonObject();

		json.addProperty("name", name);
		json.addProperty("optionType", optionType);
		json.addProperty("title", title);
		json.addProperty("required", required);
		json.addProperty("singleValue", singleValue);
		json.addProperty("description", description);

		final JsonArray optionArray = new JsonArray();

		for (final ConfigOption option : configOptions) {
			optionArray.add(option.asJson());
		}

		json.add("options", optionArray);

		final JsonArray defaultValueArray = new JsonArray();

		for (final String value : defaultValues) {
			defaultValueArray.add(value);
		}

		json.add("defaultValue", defaultValueArray);

		return json;
	}

}
