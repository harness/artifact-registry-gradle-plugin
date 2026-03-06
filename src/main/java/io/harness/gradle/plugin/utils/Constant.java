package io.harness.gradle.plugin.utils;

public class Constant {

    // Plugin tasks
    public static final String MISSING_DEPLOYMENT_PARAMETER = "Harness Plugin :: Missing deploy configuration. " +
                                                                "Required: DEPLOY_REPO_URL, DEPLOY_USERNAME, DEPLOY_TOKEN";
    public static final String PUBLISH_TASK_NAME = "harness-publish-task";
    public static final String PUBLISH_TASK_DESCRIPTION = "Collect artifacts to be later used to deploy to harness.";

    public static final String HARNESS_UNAUTHORIZED = "Harness Plugin :: Invalid credentials (401 Unauthorized)";
    public static final String HARNESS_UNABLE_TO_VERIFY = "Harness Plugin :: Unable to verify repository connection";
    public static final String DEPLOYEMENT_COMPLETE = "\n--- HARNESS  DEPLOYMENT COMPLETE ---";
    public static final String DEPLOYEMENT_STARTED = "\n--- STARTING HARNESS BATCH DEPLOYMENT ---";
}
