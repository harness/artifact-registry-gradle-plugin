package io.harness.gradle.plugin;

import io.harness.gradle.plugin.extension.HarnessPublishExtension;
import io.harness.gradle.plugin.listener.BuildInfoCollector;
import io.harness.gradle.plugin.task.DeployTask;
import io.harness.gradle.plugin.utils.Config;
import io.harness.gradle.plugin.utils.Constant;
import io.harness.gradle.plugin.utils.TaskUtil;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import org.gradle.api.component.Artifact;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.MavenPublication;

import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class HarnessDeploymentPlugin implements Plugin<Project> {

static final ConcurrentLinkedQueue<ArtifactData> deploymentQueue = new ConcurrentLinkedQueue<>();
private final BuildInfoCollector buildInfoCollector = new BuildInfoCollector();
private DeployTask deployTask;

    @Override
    public void apply(Project project) {

        if (deployTask == null) {
            deployTask = new DeployTask(project.getLogger());
        }
        
        HarnessPublishExtension extension;

        if (project == project.getRootProject()) {
            extension = project.getExtensions()
                    .create("harnesspublish", HarnessPublishExtension.class);
        } else {
            extension = project.getRootProject()
                    .getExtensions()
                    .getByType(HarnessPublishExtension.class);
        }

        //validating input param
        if (project.equals(project.getRootProject())) {

            String repoUrl = Config.resolve("DEPLOY_REPO_URL", extension.getApiUrl());
            String user    = Config.resolve("DEPLOY_USERNAME", extension.getUsername());
            String pass    = Config.resolve("DEPLOY_TOKEN", extension.getToken());

            try {
                Config.validateConfig(repoUrl, user, pass);
            } catch (Exception e) {

                if (TaskUtil.isPublishTask(project)) {
                    throw new GradleException(Constant.MISSING_DEPLOYMENT_PARAMETER);
                }
            }
        }

        //System.out.println("=============== plugin execute : " + project.getTasksByName("plugin", true).toString());
        project.getPlugins().withId("maven-publish", plugin -> {

            project.afterEvaluate(p -> {

                PublishingExtension publishing =
                        p.getExtensions().getByType(PublishingExtension.class);

                publishing.getRepositories().clear();

                publishing.getRepositories().maven(repo -> {
                    repo.setName("Harness-repository");
                    repo.setUrl(URI.create(extension.getApiUrl()));

                    repo.credentials(credentials -> {
                        credentials.setUsername(extension.getUsername());
                        credentials.setPassword(extension.getToken());
                    });
                });

                buildInfoCollector.collectPublicationInfo(p, deploymentQueue);
                buildInfoCollector.disableDefaultPublishTasks(p);

            });
        });

        if (project.equals(project.getRootProject())) {
            project.getGradle().buildFinished(result -> {

                boolean isPublishRequested =TaskUtil.isPublishTask(project);

                if (isPublishRequested) {

                    deployTask.verifyCredentials(extension.getApiUrl(),extension.getUsername(),extension.getToken());
                    deployTask.executeBatchDeployment(result, deploymentQueue);
                }
            });
        }
    }

}