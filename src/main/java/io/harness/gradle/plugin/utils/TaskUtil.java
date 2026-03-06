package io.harness.gradle.plugin.utils;

import org.gradle.api.Project;
public class TaskUtil {

    public static boolean  isPublishTask(Project project) {
        boolean result = project.getGradle().getStartParameter()
                .getTaskNames()
                .stream()
                .anyMatch(task -> task.contains("publish"));

        return result;

    }

}
