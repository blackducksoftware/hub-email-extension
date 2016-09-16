package com.blackducksoftware.integration.email.extension;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import org.restlet.data.Reference;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Java representation of an OAuth client configured on the Hub server
 *
 * @author romeara
 * @since 0.1
 */
public final class HubExtensionDescriptor {

    private final String name;

    private final String description;

    private final String clientId;

    private final Reference href;

    /**
     * Creates a single Hub client representation from a JSON record
     *
     * @param json
     *            JSON record to parse from to create the Java representation
     * @since 0.1
     */
    public HubExtensionDescriptor(JsonObject json) {
        name = json.get("name").getAsString();
        description = json.get("description").getAsString();
        clientId = json.get("clientId").getAsString();
        href = new Reference(json.get("_meta").getAsJsonObject().get("href").getAsString());
    }

    /**
     * @return Short label for quick identification of the clients from among all configured OAuth clients
     * @since 0.1
     */
    public String getName() {
        return name;
    }

    /**
     * @return Short summary of what the client represents as an application
     * @since 0.1
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The OAuth client ID used during authentication by this client
     * @since 0.1
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return Reference to the endpoint which individually describes the OAuth client
     * @since 0.1
     */
    public Reference getHref() {
        return href;
    }

    /**
     * Reads and converts JSON data representing a paged list of Hub OAuth clients into java representations
     *
     * @param json
     *            The JSon data to convert
     * @return A collection of the converted Hub OAuth clients
     * @since 0.1
     */
    public static Collection<HubExtensionDescriptor> fromPagedData(JsonObject json) {
        Collection<HubExtensionDescriptor> hubClients = Lists.newArrayList();

        JsonArray array = json.get("items").getAsJsonArray();

        for (JsonElement element : array) {
            hubClients.add(new HubExtensionDescriptor(element.getAsJsonObject()));
        }

        return hubClients;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, clientId, href);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof HubExtensionDescriptor) {
            HubExtensionDescriptor compare = (HubExtensionDescriptor) obj;

            result = Objects.equals(compare.getClientId(), getClientId())
                    && Objects.equals(compare.getDescription(), getDescription())
                    && Objects.equals(compare.getHref(), getHref())
                    && Objects.equals(compare.getName(), getName());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("name", getName())
                .add("description", getDescription())
                .add("clientId", getClientId())
                .add("href", getHref())
                .toString();
    }
}
