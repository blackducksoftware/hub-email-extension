package com.blackducksoftware.integration.email.extension;

public class ExtensionDescriptor {

	private final String name;
	private final String description;
	private final String extensionId;

	private final ResourceMetadata _meta;

	public ExtensionDescriptor(final String name, final String description, final String extensionId,
			final ResourceMetadata meta) {
		this.name = name;
		this.description = description;
		this.extensionId = extensionId;
		this._meta = meta;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getExtensionId() {
		return extensionId;
	}

	public ResourceMetadata get_meta() {
		return _meta;
	}
}
