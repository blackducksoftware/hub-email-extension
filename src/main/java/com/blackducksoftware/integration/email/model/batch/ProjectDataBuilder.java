package com.blackducksoftware.integration.email.model.batch;

import java.util.Map;
import java.util.TreeMap;

import com.blackducksoftware.integration.email.batch.processor.NotificationCategoryEnum;

public class ProjectDataBuilder {
    private String projectName;

    private String projectVersion;

    private final Map<NotificationCategoryEnum, CategoryDataBuilder> categoryBuilderMap;

    public ProjectDataBuilder() {
        categoryBuilderMap = new TreeMap<>();
    }

    public void addCategoryBuilder(final NotificationCategoryEnum category, final CategoryDataBuilder categoryBuilder) {
        categoryBuilderMap.put(category, categoryBuilder);
    }

    public void removeCategoryBuilder(final NotificationCategoryEnum category) {
        categoryBuilderMap.remove(category);
    }

    public Map<NotificationCategoryEnum, CategoryDataBuilder> getCategoryBuilderMap() {
        return categoryBuilderMap;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(final String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public ProjectData build() {
        final Map<NotificationCategoryEnum, CategoryData> categoryMap = new TreeMap<>();
        for (final Map.Entry<NotificationCategoryEnum, CategoryDataBuilder> entry : categoryBuilderMap.entrySet()) {
            categoryMap.put(entry.getKey(), entry.getValue().build());
        }
        return new ProjectData(projectName, projectVersion, categoryMap);
    }
}
