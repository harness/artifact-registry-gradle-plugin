package io.harness.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

public class ArtifactData {
    private String coords;
    private Project project;
    private MavenPublication publication;
    private MavenArtifactRepository repository;
    
    public ArtifactData(String coords, Project project, MavenPublication publication, MavenArtifactRepository repository) {
        this.coords = coords;
        this.project = project;
        this.publication = publication;
        this.repository = repository;
    }
    
    public String getCoords() {
        return coords;
    }
    
    public void setCoords(String coords) {
        this.coords = coords;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public MavenPublication getPublication() {
        return publication;
    }
    
    public void setPublication(MavenPublication publication) {
        this.publication = publication;
    }
    
    public MavenArtifactRepository getRepository() {
        return repository;
    }
    
    public void setRepository(MavenArtifactRepository repository) {
        this.repository = repository;
    }
}
