package com.blackducksoftware.integration.email.batch.processor;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;

public class PolicyOverrideProcessor extends NotificationSubProcessor<PolicyEvent> {
    private final Logger logger = LoggerFactory.getLogger(PolicyViolationProcessor.class);

    public PolicyOverrideProcessor(final SubProcessorCache<PolicyEvent> cache) {
        super(cache);
    }

    @Override
    public void process(final NotificationContentItem notification) {
        final PolicyOverrideContentItem policyOverrideContentItem = (PolicyOverrideContentItem) notification;
        for (final PolicyRule rule : policyOverrideContentItem.getPolicyRuleList()) {
            try {
                final PolicyEvent event = new PolicyEvent(ProcessingAction.REMOVE, NotificationCategoryEnum.POLICY_VIOLATION, policyOverrideContentItem, rule);
                if (getCache().hasEvent(event.getEventKey())) {
                    getCache().removeEvent(event);
                } else {
                    event.setAction(ProcessingAction.ADD);
                    event.setCategoryType(NotificationCategoryEnum.POLICY_VIOLATION_OVERRIDE);
                    getCache().addEvent(event);
                }
            } catch (URISyntaxException e) {
                logger.error("Error processing policy violation override item {} ", policyOverrideContentItem, e);
            }
        }
    }
}
