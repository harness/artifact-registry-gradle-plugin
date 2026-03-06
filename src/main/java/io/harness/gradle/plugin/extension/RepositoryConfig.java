package io.harness.gradle.plugin.extension;


import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class RepositoryConfig {

    private final Property<String> repoKey;
    private final Property<String> username;
    private final Property<String> token;

    @Inject
    public RepositoryConfig(ObjectFactory objects) {
        this.repoKey = objects.property(String.class);
        this.username = objects.property(String.class);
        this.token = objects.property(String.class);
    }

    public Property<String> getRepoKey() {
        return repoKey;
    }

    public Property<String> getUsername() {
        return username;
    }

    public Property<String> getToken() {
        return token;
    }

    public void setRepoKey(Object value) {
        this.repoKey.set(value.toString());
    }

    public void setUsername(Object value) {
        this.username.set(value.toString());
    }

    public void setToken(Object value) {
        this.token.set(value.toString());
    }
}
