package io.harness.gradle.plugin.task;

import io.harness.gradle.plugin.ArtifactData;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;

public class HarnessDeployerTask {

    // Create a temporary publish task to leverage Gradle's built-in deployment
    // This handles all checksums (MD5, SHA1, SHA256, SHA512) and maven-metadata.xml automatically

    public void deployArtifact(ArtifactData data) throws Exception {

        Project p = data.getProject();
        MavenPublication pub = data.getPublication();
        MavenArtifactRepository repo = data.getRepository();

        
        // Find or create the publish task for this publication+repository combination
        String taskName = "publishHarness" + capitalize(pub.getName()) + "PublicationTo" + capitalize(repo.getName()) + "Repository";
        
        Task publishTask = p.getTasks().findByName(taskName);
        if (publishTask == null) {
            // Create a new publish task using Gradle's PublishToMavenRepository
            publishTask = p.getTasks().create(taskName, PublishToMavenRepository.class, task -> {
                task.setPublication(pub);
                task.setRepository(repo);
            });
        }
        
        // Execute the task - this will handle all artifacts, checksums, and metadata
        if (publishTask instanceof PublishToMavenRepository) {
            PublishToMavenRepository publishToMaven = (PublishToMavenRepository) publishTask;

            publishTask.getLogger().info("Publishing to Maven repository :: " + repo.getName());
            //System.out.println("  → Publishing to Harness Repo: " + repo.getUrl());
            //System.out.println("  → Artifacts: JAR, POM, sources, javadoc (if available)");
            //System.out.println("  → Checksums: MD5, SHA1, SHA256, SHA512");
            //System.out.println("  → Metadata: maven-metadata.xml");
            
            // Execute the publish action
            publishToMaven.getActions().forEach(action -> action.execute(publishToMaven));
        }
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
