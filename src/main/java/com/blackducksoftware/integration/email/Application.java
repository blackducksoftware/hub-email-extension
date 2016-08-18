package com.blackducksoftware.integration.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.notifier.EmailEngine;

public class Application {
	private final static Logger logger = LoggerFactory.getLogger(Application.class);

	public final EmailEngine emailEngine;

	public static void main(final String[] args) {
		Thread shutDownThread = null;
		try {
			final Application app = new Application();
			shutDownThread = new Thread() {
				@Override
				public void run() {
					if (app != null) {
						if (app.emailEngine != null) {
							app.emailEngine.shutDown();
						}
					}
				}
			};

		} catch (final Exception e) {
			logger.error("Exception occured during application execution", e);
		} finally {
			if (shutDownThread != null) {
				Runtime.getRuntime().addShutdownHook(shutDownThread);
			}
		}
	}

	public Application() throws Exception {
		emailEngine = new EmailEngine();
		emailEngine.start();
	}
}
