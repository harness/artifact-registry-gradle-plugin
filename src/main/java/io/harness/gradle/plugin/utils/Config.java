package io.harness.gradle.plugin.utils;

public class Config {

    //public static volatile boolean deploymentEnabled = true;

    public static void validateConfig(String repoUrl, String user, String pass)
            throws Exception {

        if (repoUrl == null || user == null || pass == null) {
            throw new Exception(Constant.MISSING_DEPLOYMENT_PARAMETER);
        }
    }

    public static String resolve(String envKey, String fallback) {
        String env = System.getenv(envKey);
        if (env != null && !env.isEmpty()) {
            return env;
        }
        return fallback;
    }


}
