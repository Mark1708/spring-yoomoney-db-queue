package com.example.starter.properties;

import com.example.dbqueue.settings.FailRetryType;

import java.time.Duration;

public class FailureSettingsProperties {

    private FailRetryType retryType = FailRetryType.GEOMETRIC_BACKOFF;
    private Duration retryInterval = Duration.ofMinutes(1);

    public FailureSettingsProperties(FailRetryType retryType, Duration retryInterval) {
        this.retryType = retryType;
        this.retryInterval = retryInterval;
    }

    public FailureSettingsProperties() {
    }

    public FailRetryType getRetryType() {
        return retryType;
    }

    public void setRetryType(FailRetryType retryType) {
        this.retryType = retryType;
    }

    public Duration getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
    }
}
