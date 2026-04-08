package io.harness.gradle.plugin.task;

import io.harness.gradle.plugin.ArtifactData;
import io.harness.gradle.plugin.utils.ConcurrencyUtil;
import io.harness.gradle.plugin.utils.Constant;
import io.harness.gradle.plugin.utils.HarnessPluginLogger;
import org.gradle.BuildResult;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger ;

public class DeployTask {
    
    private final HarnessDeployerTask deployer = new HarnessDeployerTask();
    private final HarnessPluginLogger logger;
    
    public DeployTask(Logger logger) {
        this.logger = new HarnessPluginLogger(logger);
    }
    
    public void executeBatchDeployment(BuildResult result, ConcurrentLinkedQueue<ArtifactData> deploymentQueue) {

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        if (result.getFailure() == null && !deploymentQueue.isEmpty()) {
            logger.info(Constant.DEPLOYEMENT_STARTED);
            logger.info("main deployment skipped :: Using harness deployment");
            int totalArtifacts = deploymentQueue.size();
            logger.info("Total artifacts: " + totalArtifacts);
            //System.out.println("artifacts: " + deploymentQueue);


            ExecutorService executor = Executors.newFixedThreadPool(ConcurrencyUtil.getPoolSize(deploymentQueue,logger));
            
            while (!deploymentQueue.isEmpty()) {
                ArtifactData data = deploymentQueue.poll();
                logger.info("Deploying artifact: " + data.getCoords());
                
                executor.submit(() -> {
                    logger.debug("Submitted for Batch execution: " + data.getCoords());
                    try {
                        deployer.deployArtifact(data);
                        logger.info("✓ Successfully deployed: " + data.getCoords());
                        successCount.incrementAndGet();
                    } catch (InterruptedException e) {
                        logger.error("Interrupted: " + data.getCoords());
                        Thread.currentThread().interrupt();
                        failureCount.incrementAndGet();

                    } catch (Exception e) {
                        logger.error("✗ Failed to deploy " + data.getCoords() + ": " + e.getMessage(), e);
                        failureCount.incrementAndGet();

                    } catch (Throwable t) {
                        logger.error("Critical error: " + data.getCoords() + ": " + t.getMessage(), t);
                        failureCount.incrementAndGet();
                    }

                });
            }
            
            executor.shutdown();
            long  executorWaitTime = ConcurrencyUtil.getExecutorWaitTime(logger);
            try {
                if (!executor.awaitTermination(executorWaitTime, java.util.concurrent.TimeUnit.MINUTES)) {
                    logger.error("Executor Timeout reached. Forcing shutdown harness deployment...");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                logger.error("Deployment interrupted. Failing the build.", e);
                throw new RuntimeException("Deployment interrupted", e);
            }


            logger.info("\n--- DEPLOYMENT SUMMARY ---");
            logger.info("Total artifacts: " + totalArtifacts);
            logger.info("Successfully deployed: " + successCount.get());
            logger.info("Failed: " + failureCount.get());

            int incomplete = totalArtifacts - successCount.get() - failureCount.get();
            logger.info("Incomplete: " + incomplete);
            
            logger.info(Constant.DEPLOYEMENT_COMPLETE);
        }
    }


    public  void verifyCredentials(String repoUrl, String user, String pass) {
        try {
            URL url = new URL(repoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String encoded = Base64.getEncoder()
                    .encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));

            conn.setRequestProperty("Authorization", "Basic " + encoded);
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 401) {
                throw new GradleException(Constant.HARNESS_UNAUTHORIZED);
            }

        } catch (IOException e) {
            throw new GradleException(Constant.HARNESS_UNABLE_TO_VERIFY, e);
        }
    }
}
