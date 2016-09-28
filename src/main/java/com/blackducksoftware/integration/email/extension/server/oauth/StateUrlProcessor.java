package com.blackducksoftware.integration.email.extension.server.oauth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class StateUrlProcessor {

	private static final String REFERING_PATH_KEY = "refering_path";

	private static final String RETURN_URL_KEY = "return_url";

	private Optional<String> referingPath;

	private Optional<String> returnUrl;

	public StateUrlProcessor(final String urlState) {
		final Map<String, String> decoded = decodeMap(urlState);

		referingPath = Optional.empty();
		returnUrl = Optional.empty();

		if (decoded != null) {
			if (decoded.containsKey(REFERING_PATH_KEY)) {
				referingPath = Optional.of(decoded.get(REFERING_PATH_KEY));
			}

			if (decoded.containsKey(RETURN_URL_KEY)) {
				returnUrl = Optional.of(decoded.get(RETURN_URL_KEY));
			}
		}
	}

	public StateUrlProcessor() {
		referingPath = Optional.empty();
		returnUrl = Optional.empty();
	}

	public Optional<String> getReferingPath() {
		return referingPath;
	}

	public Optional<String> getReturnUrl() {
		return returnUrl;
	}

	public void setReferingPath(final String referingPath) {
		this.referingPath = Optional.ofNullable(referingPath);
	}

	public void setReturnUrl(final String returnUrl) {
		this.returnUrl = Optional.ofNullable(returnUrl);
	}

	public Optional<String> encode() {
		String result = null;

		if (referingPath.isPresent() || returnUrl.isPresent()) {
			final Map<String, String> stateMap = Maps.newHashMap();

			if (referingPath.isPresent()) {
				stateMap.put(REFERING_PATH_KEY, referingPath.get());
			}

			if (returnUrl.isPresent()) {
				stateMap.put(RETURN_URL_KEY, returnUrl.get());
			}

			result = encodeMap(stateMap);
		}

		return Optional.ofNullable(result);
	}

	private String encodeMap(final Map<String, String> a) {
		String result = null;

		if (a != null) {
			final Collection<String> allValues = Lists.newArrayList();

			for (final Entry<String, String> entry : a.entrySet()) {
				allValues.add(entry.getKey() + "=" + entry.getValue());
			}

			result = allValues.stream().collect(Collectors.joining(","));
			result = new String(Base64.getUrlEncoder().encode(result.getBytes(StandardCharsets.UTF_8)));
		}

		return result;
	}

	private Map<String, String> decodeMap(final String b) {
		Map<String, String> result = null;

		if (b != null) {
			result = Maps.newHashMap();

			final String encodedMap = new String(Base64.getUrlDecoder().decode(b.getBytes(StandardCharsets.UTF_8)));

			final Collection<String> allValues = Lists.newArrayList(encodedMap.split(","));

			for (final String value : allValues) {
				final String[] pair = value.split("=");

				if (pair.length == 2) {
					result.put(pair[0], pair[1]);
				}
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(referingPath, returnUrl);
	}

	@Override
	public boolean equals(@Nullable final Object obj) {
		boolean result = false;

		if (obj instanceof StateUrlProcessor) {
			final StateUrlProcessor compare = (StateUrlProcessor) obj;

			result = Objects.equals(compare.getReferingPath(), getReferingPath())
					&& Objects.equals(compare.getReturnUrl(), getReturnUrl());
		}

		return result;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).omitNullValues().add("referingPath", getReferingPath())
				.add("returnUrl", getReturnUrl()).toString();
	}
}
