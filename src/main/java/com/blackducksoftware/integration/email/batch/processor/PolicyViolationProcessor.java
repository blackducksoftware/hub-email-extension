package com.blackducksoftware.integration.email.batch.processor;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;

public class PolicyViolationProcessor extends NotificationSubProcessor<PolicyEvent> {
    private final Logger logger = LoggerFactory.getLogger(PolicyViolationProcessor.class);

    public PolicyViolationProcessor(final SubProcessorCache<PolicyEvent> cache) {
        super(cache);
    }

    @Override
    public void process(final NotificationContentItem notification) {
        if (notification instanceof PolicyViolationContentItem) {
            final PolicyViolationContentItem policyViolationContentItem = (PolicyViolationContentItem) notification;
            for (final PolicyRule rule : policyViolationContentItem.getPolicyRuleList()) {
                try {
                    PolicyEvent event = new PolicyEvent(ProcessingAction.ADD, NotificationCategoryEnum.POLICY_VIOLATION, policyViolationContentItem, rule);
                    getCache().addEvent(event);
                } catch (URISyntaxException e) {
                    logger.error("Error processing policy violation item {} ", policyViolationContentItem, e);
                }
            }
        }
    }
}
