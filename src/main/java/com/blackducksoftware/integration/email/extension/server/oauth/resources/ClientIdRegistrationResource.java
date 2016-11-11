package com.blackducksoftware.integration.email.extension.server.oauth.resources;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;

import com.blackducksoftware.integration.email.extension.server.oauth.TokenManager;

public class ClientIdRegistrationResource extends OAuthServerResource {

    @Post
    public void accept(final JsonRepresentation entity) {
        final String clientId = entity.getJsonObject().getString("clientId");

        if (StringUtils.isNotBlank(clientId)) {
            final TokenManager tokenManager = getTokenManager();
            if (tokenManager != null) {
                getTokenManager().updateClientId(clientId);
            } else {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No client ID/hub URL provided");
        }
    }
}
