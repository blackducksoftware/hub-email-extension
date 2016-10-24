package com.blackducksoftware.integration.email.batch.processor;

import java.net.URISyntaxException;
import java.util.Set;

import com.blackducksoftware.integration.email.model.batch.ItemEntry;
import com.blackducksoftware.integration.hub.dataservices.notification.items.NotificationContentItem;

public abstract class NotificationEvent<T extends NotificationContentItem> {
    private ProcessingAction action;

    private NotificationCategoryEnum categoryType;

    private final T notificationContent;

    private Set<ItemEntry> dataSet;

    private String eventKey;

    public NotificationEvent(final ProcessingAction action, final NotificationCategoryEnum categoryType, T notificationContent) {
        this.action = action;
        this.categoryType = categoryType;
        this.notificationContent = notificationContent;
    }

    public void init() throws URISyntaxException {
        dataSet = generateDataSet();
        eventKey = generateEventKey();
    }

    public String hashString(final String origString) {
        String hashString;
        if (origString == null) {
            hashString = "";
        } else {
            hashString = String.valueOf(origString.hashCode());
        }
        return hashString;
    }

    public abstract Set<ItemEntry> generateDataSet();

    public abstract String generateEventKey() throws URISyntaxException;

    public abstract int countCategoryItems();

    public ProcessingAction getAction() {
        return action;
    }

    public void setAction(ProcessingAction action) {
        this.action = action;
    }

    public NotificationCategoryEnum getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(NotificationCategoryEnum categoryType) {
        this.categoryType = categoryType;
    }

    public T getNotificationContent() {
        return notificationContent;
    }

    public Set<ItemEntry> getDataSet() {
        return dataSet;
    }

    public String getEventKey() {
        return eventKey;
    }
}
