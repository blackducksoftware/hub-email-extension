FROM java:openjdk-8-jre-alpine

ARG VERSION

LABEL com.blackducksoftware.integration.email.vendor="Black Duck Software, Inc." \ 
      com.blackducksoftware.integration.email.version="$VERSION"
      
ENV EMAIL_EXT_HOME="/opt/blackduck/extensions/hub-email-extension"
ENV PATH "$EMAIL_EXT_HOME/bin:$PATH"

ADD build/distributions/hub-email-extension-$VERSION.tar "/opt/blackduck/extensions/"

# ports to expose for this image
EXPOSE 55000

RUN set -e \
    && apk update \
    && apk add bash \
    && mv "$EMAIL_EXT_HOME-$VERSION" "$EMAIL_EXT_HOME" \
    && addgroup -S emailextension \
    && adduser -h "$EMAIL_EXT_HOME" -g emailextension -s /sbin/nologin -G emailextension -S -D -H emailextension \
    && chown -R emailextension:emailextension "$EMAIL_EXT_HOME" \
    && chmod 744 "$EMAIL_EXT_HOME/bin/email_extension.sh"

USER emailextension
ENTRYPOINT ["/bin/bash", "-c"] 
CMD ["/opt/blackduck/extensions/hub-email-extension/bin/email_extension.sh", "start"]