package com.blackducksoftware.integration.email.extension.server.api.model;

public class ResourceLink {

    private final String rel;

    private final String href;

    public ResourceLink(final String rel, final String href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public String getHref() {
        return href;
    }
}
