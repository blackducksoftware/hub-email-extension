package com.blackducksoftware.integration.email.extension.server.api.model;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class HubOAuthClient {

	private final String name;
	private final String description;
	private final String clientId;
	private final String href;

	public HubOAuthClient(final JsonObject json) {
		name = json.get("name").getAsString();
		description = json.get("description").getAsString();
		clientId = json.get("clientId").getAsString();
		href = json.get("_meta").getAsJsonObject().get("href").getAsString();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getClientId() {
		return clientId;
	}

	public String getHref() {
		return href;
	}

	public static Collection<HubOAuthClient> fromPagedData(final JsonObject json) {
		final Collection<HubOAuthClient> hubClients = Lists.newArrayList();

		final JsonArray array = json.get("items").getAsJsonArray();

		for (final JsonElement element : array) {
			hubClients.add(new HubOAuthClient(element.getAsJsonObject()));
		}

		return hubClients;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, clientId, href);
	}

	@Override
	public boolean equals(@Nullable final Object obj) {
		boolean result = false;

		if (obj instanceof HubOAuthClient) {
			final HubOAuthClient compare = (HubOAuthClient) obj;

			result = Objects.equals(compare.getClientId(), getClientId())
					&& Objects.equals(compare.getDescription(), getDescription())
					&& Objects.equals(compare.getHref(), getHref()) && Objects.equals(compare.getName(), getName());
		}

		return result;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).omitNullValues().add("name", getName())
				.add("description", getDescription()).add("clientId", getClientId()).add("href", getHref()).toString();
	}
}
