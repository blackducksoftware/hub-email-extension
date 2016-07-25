package com.blackducksoftware.integration.email;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.email.service.ConfigurationResponseParser;
import com.blackducksoftware.integration.email.service.properties.ServicePropertiesBuilder;
import com.google.gson.Gson;

@SpringBootApplication
public class Application {

	public final static String ARG_PROP_FILE = "propertyFile";

	public static void main(final String[] args) {
		try {
			final CommandLine cmd = parseCommandLine(args);
			final ServicePropertiesBuilder propertyBuilder = new ServicePropertiesBuilder();

			if (cmd.hasOption(ARG_PROP_FILE)) {
				final String path = cmd.getOptionValue(ARG_PROP_FILE);
				propertyBuilder.setFilePath(path);
			}

			final Properties props = propertyBuilder.build();

			SpringApplication.run(Application.class, args);
		} catch (final ParseException | IOException e) {
			e.printStackTrace();
		}
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

	private static Options createCommandLineOptions() {
		final Options options = new Options();
		final Option option = new Option(ARG_PROP_FILE, true,
				"The path to the file containing the properties for the service.");
		option.setArgName("file");

		options.addOption(option);
		return options;
	}

	private static CommandLine parseCommandLine(final String[] args) throws ParseException {
		final Options options = createCommandLineOptions();
		final CommandLineParser parser = new DefaultParser();
		return parser.parse(options, args);
	}
}
