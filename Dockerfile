FROM openjdk:8-jre-alpine

ARG VERSION

LABEL com.blackducksoftware.integration.email.vendor="Black Duck Software, Inc." \
      com.blackducksoftware.integration.email.version="$VERSION"

ENV EMAIL_EXT_HOME /blackduck-extensions-tar/hub-email-extension-$VERSION
ENV PATH $EMAIL_EXT_HOME/bin:$PATH

RUN set -e \
    # The old version of the Gradle Application plugin generates Bash scripts
    && apk add --no-cache --virtual .hub-ext-rundeps bash \
    && addgroup -S hubext \
    && adduser -h "$EMAIL_EXT_HOME" -g hubext -s /sbin/nologin -G hubext -S -D -H hubext

ADD "build/distributions/hub-email-extension-$VERSION.tar" " /blackduck-extensions-tar/

# Override the default logger settings to match other Hub containers

RUN mkdir -p /blackduck-extensions-config-defaults

VOLUME [ "/blackduck-extensions-config-volume/" ]

# The app itself will read in from the -volume directory at runtime.  We write these to an
# easily accessible location that the entrypoint can always find and copy data from.
COPY "src/docker/resources/log4j.xml" "/blackduck-extensions-config-defaults/log4j.xml"
RUN cp -r /blackduck-extensions-tar/blackduck-extensions-config-defaults/* /blackduck-extensions-config-defaults/

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 55000

ENTRYPOINT [ "docker-entrypoint.sh" ]

CMD [ "hub-email-extension" ]
