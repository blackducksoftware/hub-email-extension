package com.blackducksoftware.integration.email.extension.server.api.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConfigOption {

    private String name;

    private String title;

    public ConfigOption(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public JsonElement asJson() {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("title", title);

        return json;
    }
}
