package com.blackducksoftware.integration.email.batch.processor;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationClearedContentItem;

public class PolicyViolationClearedProcessor extends NotificationSubProcessor<PolicyEvent> {
    private final Logger logger = LoggerFactory.getLogger(PolicyViolationProcessor.class);

    public PolicyViolationClearedProcessor(final SubProcessorCache<PolicyEvent> cache) {
        super(cache);
    }

    @Override
    public void process(final NotificationContentItem notification) {
        if (notification instanceof PolicyViolationClearedContentItem) {
            final PolicyViolationClearedContentItem policyViolationCleared = (PolicyViolationClearedContentItem) notification;
            for (final PolicyRule rule : policyViolationCleared.getPolicyRuleList()) {
                try {
                    final PolicyEvent event = new PolicyEvent(ProcessingAction.REMOVE, NotificationCategoryEnum.POLICY_VIOLATION, policyViolationCleared, rule);
                    if (getCache().hasEvent(event.getEventKey())) {
                        getCache().removeEvent(event);
                    } else {
                        event.setAction(ProcessingAction.ADD);
                        event.setCategoryType(NotificationCategoryEnum.POLICY_VIOLATION_CLEARED);
                        getCache().addEvent(event);
                    }
                } catch (URISyntaxException e) {
                    logger.error("Error processing policy violation cleared item {} ", policyViolationCleared, e);
                }
            }
        }
    }
}
