package io.harness.gradle.plugin.utils;

import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

public class HarnessPluginLogger {

    private final Logger logger;

    public HarnessPluginLogger(Logger logger) {
        this.logger = logger;
    }

    public void debug(String message) {
        this.logger.log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        this.logger.log(LogLevel.LIFECYCLE, message);
    }

    public void warn(String message) {
        this.logger.log(LogLevel.WARN, message);
    }

    public void error(String message) {
        this.logger.log(LogLevel.ERROR, message);
    }

    public void error(String message, Throwable e) {
        this.logger.log(LogLevel.ERROR, message, e);
    }
}
