package io.harness.gradle.plugin.utils;

import io.harness.gradle.plugin.ArtifactData;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ConcurrencyUtil {


    public static int getPoolSize(ConcurrentLinkedQueue<ArtifactData> requestsQueue) {

        int threads=0;
        int defaultAvailableThreads = Runtime.getRuntime().availableProcessors();
        String threadCountEnv = System.getenv("DEPLOY_THREAD_COUNT");
        if (threadCountEnv != null && !threadCountEnv.isEmpty()) {
            try {
                threads = Integer.parseInt(threadCountEnv);
                threads = Math.min(threads,Math.min(8*defaultAvailableThreads, requestsQueue.size()));
                //log.info("Using configured thread count: " + threads);
            } catch (NumberFormatException e) {
                //log.warn("Invalid DEPLOY_THREAD_COUNT " + threadCountEnv);
            }
        }

        if(threads==0) {
            threads = Math.min(4 * defaultAvailableThreads , requestsQueue.size());
        }
        return threads;
    }
}
