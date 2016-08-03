package com.blackducksoftware.integration.email.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.model.CustomerProperties;
import com.blackducksoftware.integration.email.model.EmailSystemProperties;
import com.blackducksoftware.integration.email.model.MimeMultipartBuilder;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class EmailMessagingService {
	private final Logger log = LoggerFactory.getLogger(EmailMessagingService.class);

	private final EmailSystemProperties emailSystemProperties;
	private final Configuration configuration;

	public EmailMessagingService(final EmailSystemProperties emailSystemProperties, final Configuration configuration) {
		this.emailSystemProperties = emailSystemProperties;
		this.configuration = configuration;

	}

	public void sendEmailMessage(final CustomerProperties customerProperties, final List<String> emailAddresses,
			final Map<String, Object> model, final String templateName) throws MessagingException,
			TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		final Session session = createMailSession(customerProperties);
		final Map<String, String> contentIdsToFilePaths = new HashMap<>();
		populateModelWithAdditionalProperties(customerProperties, model, templateName, contentIdsToFilePaths);
		final String html = getResolvedTemplate(model, templateName);

		final MimeMultipartBuilder mimeMultipartBuilder = new MimeMultipartBuilder();
		mimeMultipartBuilder.addHtmlContent(html);
		mimeMultipartBuilder.addTextContent(Jsoup.parse(html).text());
		mimeMultipartBuilder.addEmbeddedImages(contentIdsToFilePaths);
		final MimeMultipart mimeMultipart = mimeMultipartBuilder.build();

		final Message message = createMessage(emailAddresses, session, mimeMultipart);
		sendMessage(customerProperties, session, message);
	}

	private String getResolvedTemplate(final Map<String, Object> model, final String templateName)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
			TemplateException {
		final StringWriter stringWriter = new StringWriter();
		final Template template = configuration.getTemplate(templateName);
		template.process(model, stringWriter);
		return stringWriter.toString();
	}

	private void populateModelWithAdditionalProperties(final CustomerProperties customerProperties,
			final Map<String, Object> model, final String templateName,
			final Map<String, String> contentIdsToFilePaths) {
		for (final Map.Entry<String, String> entry : customerProperties.getSuppliedTemplateVariableProperties()
				.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			manageKey(model, templateName, contentIdsToFilePaths, key, value);
		}
	}

	private void manageKey(final Map<String, Object> model, final String templateName,
			final Map<String, String> contentIdsToFilePaths, String key, final String value) {
		boolean shouldAddToModel = false;
		if (key.contains("all.templates")) {
			shouldAddToModel = true;
			key = key.replace("all.templates.", "");
		} else if (key.contains(templateName)) {
			shouldAddToModel = true;
			key = key.replace(templateName + ".", "");
		}

		if (shouldAddToModel) {
			if (key.endsWith(".image")) {
				final String cid = generateContentId(key);
				model.put(cleanForFreemarker(key), cid);
				contentIdsToFilePaths.put("<" + cid + ">", value);
			} else {
				model.put(cleanForFreemarker(key), value);
			}
		}
	}

	private Session createMailSession(final CustomerProperties customerProperties) {
		final Map<String, String> sessionProps = customerProperties.getPropertiesForSession();
		final Properties props = new Properties();
		props.putAll(sessionProps);

		return Session.getInstance(props);
	}

	private Message createMessage(final List<String> recipientEmailAddresses, final Session session,
			final MimeMultipart mimeMultipart) throws MessagingException {
		final List<InternetAddress> addresses = new ArrayList<>();
		for (final String recipient : recipientEmailAddresses) {
			try {
				addresses.add(new InternetAddress(recipient));
			} catch (final AddressException e) {
				log.warn(String.format("Could not create the address from %s: %s", recipient, e.getMessage()));
			}
		}

		if (addresses.isEmpty()) {
			throw new RuntimeException("There were no valid email addresses supplied.");
		}

		final Message message = new MimeMessage(session);
		message.setContent(mimeMultipart);

		message.setFrom(new InternetAddress(emailSystemProperties.getEmailFromAddress()));
		message.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[addresses.size()]));
		message.setSubject("Testing Hub Email Extension");

		return message;
	}

	private void sendMessage(final CustomerProperties customerProperties, final Session session, final Message message)
			throws MessagingException {
		if (customerProperties.isAuth()) {
			sendAuthenticated(customerProperties, message, session);
		} else {
			Transport.send(message);
		}
	}

	private void sendAuthenticated(final CustomerProperties customerProperties, final Message message,
			final Session session) throws MessagingException {
		final String host = customerProperties.getHost();
		final int port = customerProperties.getPort();
		final String username = customerProperties.getUsername();
		final String password = customerProperties.getPassword();

		final Transport transport = session.getTransport("smtp");
		try {
			transport.connect(host, port, username, password);
			transport.sendMessage(message, message.getAllRecipients());
		} finally {
			transport.close();
		}
	}

	private String generateContentId(final String value) {
		final String cid = value.replaceAll("[^A-Za-z0-9]", "bd").trim() + "@blackducksoftware.com";
		return cid;
	}

	private String cleanForFreemarker(final String s) {
		return s.replace(".", "_");
	}

}
