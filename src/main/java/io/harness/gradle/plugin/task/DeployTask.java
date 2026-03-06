package io.harness.gradle.plugin.task;

import io.harness.gradle.plugin.ArtifactData;
import io.harness.gradle.plugin.utils.ConcurrencyUtil;
import io.harness.gradle.plugin.utils.Constant;
import org.gradle.BuildResult;
import org.gradle.api.GradleException;

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

public class DeployTask {
    
    private final HarnessDeployerTask deployer = new HarnessDeployerTask();
    
    public void executeBatchDeployment(BuildResult result, ConcurrentLinkedQueue<ArtifactData> deploymentQueue) {
        if (result.getFailure() == null && !deploymentQueue.isEmpty()) {
            System.out.println(Constant.DEPLOYEMENT_STARTED);
            System.out.println("main deployment skipped :: Using harness deployment");
            //System.out.println("Total artifacts: " + deploymentQueue.size());
            //System.out.println("artifacts: " + deploymentQueue);


            ExecutorService executor = Executors.newFixedThreadPool(ConcurrencyUtil.getPoolSize(deploymentQueue));
            
            while (!deploymentQueue.isEmpty()) {
                ArtifactData data = deploymentQueue.poll();
                System.out.println("Deploying artifact: " + data.getCoords());
                
                executor.submit(() -> {
                    try {
                        deployer.deployArtifact(data);
                        System.out.println("✓ Successfully deployed: " + data.getCoords());
                    } catch (Exception e) {
                        System.err.println("✗ Failed to deploy " + data.getCoords() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
            
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            System.out.println(Constant.DEPLOYEMENT_COMPLETE);
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
