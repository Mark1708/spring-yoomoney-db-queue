package com.example.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.db-queue")
public class DbQueueProperties {

    private Boolean enabled = false;
    private Map<String, QueueConfigurationProperties> config = new HashMap<>();

    public DbQueueProperties(Boolean enabled, Map<String, QueueConfigurationProperties> config) {
        this.enabled = enabled;
        this.config = config;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, QueueConfigurationProperties> getConfig() {
        return config;
    }

    public void setConfig(Map<String, QueueConfigurationProperties> config) {
        this.config = config;
    }
}
