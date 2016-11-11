package com.blackducksoftware.integration.email.extension.config;

public class ExtensionInfo {

    private final String id;

    private final String name;

    private final String description;

    private final String baseUrl;

    public ExtensionInfo(final String id, final String name, final String description, final String baseUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseUrl = baseUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
