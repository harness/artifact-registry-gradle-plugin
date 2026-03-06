package io.harness.gradle.plugin.extension;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

public class DefaultsConfig {

    private final ListProperty<String> publications;

    @Inject
    public DefaultsConfig(ObjectFactory objects) {
        this.publications = objects.listProperty(String.class);
    }

    public ListProperty<String> getPublications() {
        return publications;
    }

    public void publications(String... names) {
        publications.addAll(names);
    }
}

