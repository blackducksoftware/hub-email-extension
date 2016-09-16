package com.blackducksoftware.integration.email.extension.oauth.server;

import org.restlet.Component;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.security.AuthenticatorHelper;

public class OAuthEndpoint extends Component {

	public OAuthEndpoint(final AbstractOAuthApplication application) {
		super();

		getDefaultHost().attachDefault(application);

		getClients().add(Protocol.FILE);
		getClients().add(Protocol.HTTP);
		// Prevent warnings about unsupported authentication
		Engine.getInstance().getRegisteredAuthenticators()
				.add(new AuthenticatorHelper(ChallengeScheme.HTTP_OAUTH_BEARER, true, false) {
				});
	}
}
