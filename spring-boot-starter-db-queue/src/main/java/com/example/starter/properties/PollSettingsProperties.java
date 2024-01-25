package com.example.starter.properties;

import java.time.Duration;

public class PollSettingsProperties {

    private Duration betweenTaskTimeout = Duration.ofSeconds(1L);
    private Duration noTaskTimeout = Duration.ofSeconds(5L);
    private Duration fatalCrashTimeout = Duration.ofSeconds(1L);
    private Integer batchSize = 1;

    public PollSettingsProperties(Duration betweenTaskTimeout, Duration noTaskTimeout, Duration fatalCrashTimeout, Integer batchSize) {
        this.betweenTaskTimeout = betweenTaskTimeout;
        this.noTaskTimeout = noTaskTimeout;
        this.fatalCrashTimeout = fatalCrashTimeout;
        this.batchSize = batchSize;
    }

    public PollSettingsProperties() {
    }

    public Duration getBetweenTaskTimeout() {
        return betweenTaskTimeout;
    }

    public void setBetweenTaskTimeout(Duration betweenTaskTimeout) {
        this.betweenTaskTimeout = betweenTaskTimeout;
    }

    public Duration getNoTaskTimeout() {
        return noTaskTimeout;
    }

    public void setNoTaskTimeout(Duration noTaskTimeout) {
        this.noTaskTimeout = noTaskTimeout;
    }

    public Duration getFatalCrashTimeout() {
        return fatalCrashTimeout;
    }

    public void setFatalCrashTimeout(Duration fatalCrashTimeout) {
        this.fatalCrashTimeout = fatalCrashTimeout;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
