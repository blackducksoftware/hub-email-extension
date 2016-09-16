package com.blackducksoftware.integration.email.extension.server.api.model;

import java.util.List;

public class ResourceMetadata {

    private String href;

    private List<ResourceLink> links;

    public ResourceMetadata(String href, List<ResourceLink> links) {
        this.href = href;
        this.links = links;
    }

    public String getHref() {
        return href;
    }

    public List<ResourceLink> getLinks() {
        return links;
    }
}
