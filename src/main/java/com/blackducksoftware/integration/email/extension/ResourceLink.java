package com.blackducksoftware.integration.email.extension;

public class ResourceLink {

    private String rel;

    private String href;

    public ResourceLink(String rel, String href) {
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
