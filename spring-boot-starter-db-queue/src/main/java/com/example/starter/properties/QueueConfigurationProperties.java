package com.example.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.db-queue.config")
public class QueueConfigurationProperties {

    private String location = "queue_tasks";
    private List<String> shards = List.of("main");

    private ProcessingSettingsProperties processingSettings = new ProcessingSettingsProperties();
    private PollSettingsProperties pollSettings = new PollSettingsProperties();
    private FailureSettingsProperties failureSettings = new FailureSettingsProperties();
    private ReenqueueSettingsProperties reenqueueSettings = new ReenqueueSettingsProperties();
    private ExtSettingsProperties extSettings = new ExtSettingsProperties();

    public QueueConfigurationProperties(
            String location, List<String> shards,
            ProcessingSettingsProperties processingSettings,
            PollSettingsProperties pollSettings,
            FailureSettingsProperties failureSettings,
            ReenqueueSettingsProperties reenqueueSettings,
            ExtSettingsProperties extSettings
    ) {
        this.location = location;
        this.shards = shards;
        this.processingSettings = processingSettings;
        this.pollSettings = pollSettings;
        this.failureSettings = failureSettings;
        this.reenqueueSettings = reenqueueSettings;
        this.extSettings = extSettings;
    }

    public QueueConfigurationProperties() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getShards() {
        return shards;
    }

    public void setShards(List<String> shards) {
        this.shards = shards;
    }

    public ProcessingSettingsProperties getProcessingSettings() {
        return processingSettings;
    }

    public void setProcessingSettings(ProcessingSettingsProperties processingSettings) {
        this.processingSettings = processingSettings;
    }

    public PollSettingsProperties getPollSettings() {
        return pollSettings;
    }

    public void setPollSettings(PollSettingsProperties pollSettings) {
        this.pollSettings = pollSettings;
    }

    public FailureSettingsProperties getFailureSettings() {
        return failureSettings;
    }

    public void setFailureSettings(FailureSettingsProperties failureSettings) {
        this.failureSettings = failureSettings;
    }

    public ReenqueueSettingsProperties getReenqueueSettings() {
        return reenqueueSettings;
    }

    public void setReenqueueSettings(ReenqueueSettingsProperties reenqueueSettings) {
        this.reenqueueSettings = reenqueueSettings;
    }

    public ExtSettingsProperties getExtSettings() {
        return extSettings;
    }

    public void setExtSettings(ExtSettingsProperties extSettings) {
        this.extSettings = extSettings;
    }
}
