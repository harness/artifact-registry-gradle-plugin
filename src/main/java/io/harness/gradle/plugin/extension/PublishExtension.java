package io.harness.gradle.plugin.extension;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class PublishExtension {

    private final RepositoryConfig repository;
    private final DefaultsConfig defaults;
    private final Property<String> apiUrl;

    @Inject
    public PublishExtension(ObjectFactory objects) {
        this.apiUrl = objects.property(String.class);
        this.repository = objects.newInstance(RepositoryConfig.class);
        this.defaults = objects.newInstance(DefaultsConfig.class);
    }

    public Property<String> getApiUrl() {
        return apiUrl;
    }

    public RepositoryConfig getRepository() {
        return repository;
    }

    public DefaultsConfig getDefaults() {
        return defaults;
    }

    public void repository(Action<? super RepositoryConfig> action) {
        action.execute(repository);
    }

    public void defaults(Action<? super DefaultsConfig> action) {
        action.execute(defaults);
    }

    public void setApiUrl(Object value) {
        this.apiUrl.set(value.toString());
    }
}
