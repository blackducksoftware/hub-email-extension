package com.blackducksoftware.integration.email.notifier;

import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.ExtensionLogger;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.api.extension.ConfigurationItem;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.dataservices.extension.ExtensionConfigDataService;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;

public abstract class AbstractNotifier extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(AbstractNotifier.class);

    private final ExtensionProperties extensionProperties;

    private final EmailMessagingService emailMessagingService;

    private final DataServicesFactory dataServicesFactory;

    private String hubExtensionUri;

    private final ExtensionConfigDataService extensionConfigDataService;

    public AbstractNotifier(final ExtensionProperties extensionProperties,
            final EmailMessagingService emailMessagingService, final DataServicesFactory dataServicesFactory) {
        this.extensionProperties = extensionProperties;
        this.emailMessagingService = emailMessagingService;
        this.dataServicesFactory = dataServicesFactory;
        final ExtensionLogger extLogger = new ExtensionLogger(logger);
        extensionConfigDataService = dataServicesFactory.createExtensionConfigDataService(extLogger);
    }

    public ExtensionProperties createPropertiesFromGlobalConfig() throws UnexpectedHubResponseException {
        final Map<String, ConfigurationItem> globalMap = extensionConfigDataService
                .getGlobalConfigMap(getHubExtensionUri());
        final Properties globalProperties = new Properties();
        for (final Map.Entry<String, ConfigurationItem> entry : globalMap.entrySet()) {
            globalProperties.put(entry.getKey(), entry.getValue().getValue().get(0));
        }
        return new ExtensionProperties(globalProperties);
    }

    public abstract String getTemplateName();

    public abstract String getCronExpression();

    public abstract String getNotifierPropertyKey();

    public long getStartDelayMilliseconds() {
        return 0;
    }

    public ExtensionProperties getExtensionProperties() {
        return extensionProperties;
    }

    public EmailMessagingService getEmailMessagingService() {
        return emailMessagingService;
    }

    public DataServicesFactory getDataServicesFactory() {
        return dataServicesFactory;
    }

    public String getName() {
        return getClass().getName();
    }

    public String getHubExtensionUri() {
        return hubExtensionUri;
    }

    public void setHubExtensionUri(final String hubExtensionUri) {
        this.hubExtensionUri = hubExtensionUri;
    }

    public ExtensionConfigDataService getExtensionConfigDataService() {
        return extensionConfigDataService;
    }
}
