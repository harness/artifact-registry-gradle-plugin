package io.harness.gradle.plugin.utils;

import io.harness.gradle.plugin.ArtifactData;

import java.util.concurrent.ConcurrentLinkedQueue;

import static io.harness.gradle.plugin.utils.Constant.*;


public class ConcurrencyUtil {

    /*
     used to set default number fo thread, which also can be provided from environment variable DEPLOY_THREAD_COUNT
     */
    public static int getPoolSize(ConcurrentLinkedQueue<ArtifactData> requestsQueue,HarnessPluginLogger logger) {

        int threads=0;
        int defaultAvailableThreads = Runtime.getRuntime().availableProcessors();
        logger.info("Default Available Threads : " + defaultAvailableThreads);
        String threadCountEnv = System.getenv("DEPLOY_THREAD_COUNT");
        if (threadCountEnv != null && !threadCountEnv.isEmpty()) {
            try {
                threads = Integer.parseInt(threadCountEnv);
                threads = Math.min(threads,Math.min( 8 * defaultAvailableThreads, requestsQueue.size()));
                logger.info("Using configured thread count: " + threads);
            } catch (NumberFormatException e) {
                logger.error("Invalid DEPLOY_THREAD_COUNT " + threadCountEnv + e.getMessage());
            }
        }

        if(threads==0) {
            threads = Math.min(4 * defaultAvailableThreads , requestsQueue.size());
        }
        return threads;
    }

    /*
     used to set default Execution deployment time , which also can be provided from environment variable DEPLOYMENT_TIME
     */
    public static long getExecutorWaitTime(HarnessPluginLogger logger) {

        String deploymentTimeEnv = System.getenv("DEPLOYMENT_TIME");
        long waitTime;

        if (deploymentTimeEnv == null || deploymentTimeEnv.isEmpty()) {
            logger.info("Setting default Execution time!! " + DEFAULT_EXECUTOR_TIME);
            waitTime = DEFAULT_EXECUTOR_TIME;
        } else {
            try {
                waitTime = Long.parseLong(deploymentTimeEnv);

                waitTime = Math.min(waitTime, DEFAULT_EXECUTOR_MAXTIME);

                logger.info("Setting Execution time as provided :" + waitTime);
            } catch (NumberFormatException e) {
                logger.info("Invalid Execution time  provided :" + deploymentTimeEnv);
                logger.info("Setting default Execution time!! " + DEFAULT_EXECUTOR_TIME);
                waitTime = DEFAULT_EXECUTOR_TIME;
            }
        }

        if (waitTime <= DEFAULT_EXECUTOR_MINTIME) {
            waitTime = DEFAULT_EXECUTOR_MINTIME;
            logger.info("Execution time cannot be less than : " + DEFAULT_EXECUTOR_MINTIME);
        }

        return waitTime;
    }
}
