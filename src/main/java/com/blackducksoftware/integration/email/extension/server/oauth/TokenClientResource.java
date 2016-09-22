package com.blackducksoftware.integration.email.extension.server.oauth;

import java.net.URI;
import java.util.Objects;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.ext.oauth.OAuthResourceDefs;
import org.restlet.ext.oauth.internal.Token;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class TokenClientResource extends ClientResource implements OAuthResourceDefs {

	private final Token token;

	public TokenClientResource(final URI uri, final Token token) {
		super(uri);
		this.token = Objects.requireNonNull(token);
	}

	@Override
	public Response handleOutbound(final Request request) {
		if (token.getTokenType().equalsIgnoreCase(TOKEN_TYPE_BEARER)) {
			final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_OAUTH_BEARER);
			cr.setRawValue(token.getAccessToken());
			request.setChallengeResponse(cr);
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "Unsupported token type.");
		}

		return super.handleOutbound(request);
	}
}
