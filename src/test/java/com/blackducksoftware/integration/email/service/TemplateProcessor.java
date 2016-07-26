package com.blackducksoftware.integration.email.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Component
public class TemplateProcessor {
	@Autowired
	private Configuration configuration;

	public String getResolvedTemplate(final Map<String, Object> model, final String templateName)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
			TemplateException {
		final StringWriter stringWriter = new StringWriter();
		final Template template = configuration.getTemplate("htmlTemplate.ftl");
		template.process(model, stringWriter);
		return stringWriter.toString();
	}

}
