package io.harness.gradle.plugin.listener;

import io.harness.gradle.plugin.ArtifactData;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BuildInfoCollector {
    
    public void collectPublicationInfo(Project project, ConcurrentLinkedQueue<ArtifactData> deploymentQueue) {
        PublishingExtension publishing = project.getExtensions().findByType(PublishingExtension.class);
        
        if (publishing != null) {
            publishing.getPublications().withType(MavenPublication.class).all(mavenPub -> {
                publishing.getRepositories().withType(MavenArtifactRepository.class).all(repo -> {
                    String info = mavenPub.getGroupId() + ":" + mavenPub.getArtifactId() + ":" + mavenPub.getVersion();
                    
                    ArtifactData data = new ArtifactData(info, project, mavenPub, repo);
                    deploymentQueue.add(data);

                    project.getLogger().lifecycle("Harness Queued: " + info + " from project " + project.getName() + " -> " + repo.getName());
                    //System.out.println("Harness Queued: " + info + " from project " + project.getName() + " -> " + repo.getName());
                });
            });
        }
    }
    
    public void disableDefaultPublishTasks(Project project) {
        PublishingExtension publishing = project.getExtensions().findByType(PublishingExtension.class);
        
        if (publishing != null) {
            project.getTasks().matching(task ->
                    task.getName().startsWith("publish")
            ).configureEach(task -> {
                task.setEnabled(false);
            });
        }
    }
}
