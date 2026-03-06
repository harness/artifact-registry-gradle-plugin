package io.harness.gradle.plugin.extension;


public class HarnessPublishExtension {

    private String apiUrl;
    private String username;
    private String token;

    // getters
    public String getApiUrl() {
        return apiUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    // setters
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

