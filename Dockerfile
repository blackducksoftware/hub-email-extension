FROM openjdk:8-jre-alpine

ARG VERSION

LABEL com.blackducksoftware.integration.email.vendor="Black Duck Software, Inc." \
      com.blackducksoftware.integration.email.version="$VERSION"

ENV EMAIL_EXT_HOME /opt/blackduck/extensions/hub-email-extension-$VERSION
ENV PATH $EMAIL_EXT_HOME/bin:$PATH

RUN set -e \
    # The old version of the Gradle Application plugin generates Bash scripts
    && apk add --no-cache --virtual .hub-ext-rundeps bash curl jq openssl \
    && addgroup -S hubext \
    && adduser -h "$EMAIL_EXT_HOME" -g hubext -s /sbin/nologin -G hubext -S -D -H hubext

ADD "build/distributions/hub-email-extension-$VERSION.tar" "/opt/blackduck/extensions/"

# Override the default logger settings to match other Hub containers
COPY "src/docker/resources/log4j.xml" "$EMAIL_EXT_HOME/config"

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod +x /usr/local/bin/docker-entrypoint.sh

VOLUME [ "/opt/blackduck/extensions" ]

EXPOSE 55000

ENTRYPOINT [ "docker-entrypoint.sh" ]

CMD [ "hub-email-extension" ]
