package com.blackducksoftware.integration.email;

import com.blackducksoftware.integration.email.notifier.EmailEngine;

public class Application {
	public final EmailEngine emailEngine;

	public static void main(final String[] args) {
		try {
			new Application();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public Application() throws Exception {
		emailEngine = new EmailEngine();
	}

}
