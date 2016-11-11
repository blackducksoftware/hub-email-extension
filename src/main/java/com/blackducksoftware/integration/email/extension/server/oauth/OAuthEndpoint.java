package com.blackducksoftware.integration.email.extension.server.oauth;

import org.restlet.Component;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.security.AuthenticatorHelper;

public class OAuthEndpoint extends Component {

    public OAuthEndpoint(final AbstractOAuthApplication application) {

        getDefaultHost().attachDefault(application);

        getClients().add(Protocol.FILE);
        getClients().add(Protocol.HTTP);
        getClients().add(Protocol.HTTPS);
        // Prevent warnings about unsupported authentication
        Engine.getInstance().getRegisteredAuthenticators()
                .add(new AuthenticatorHelper(ChallengeScheme.HTTP_OAUTH_BEARER, true, false) {
                });
    }
}
