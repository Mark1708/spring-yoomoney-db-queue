package com.example.starter.properties;

import com.example.dbqueue.settings.ReenqueueRetryType;

public class ReenqueueSettingsProperties {

    private ReenqueueRetryType retryType = ReenqueueRetryType.MANUAL;

    public ReenqueueSettingsProperties(ReenqueueRetryType retryType) {
        this.retryType = retryType;
    }

    public ReenqueueSettingsProperties() {
    }

    public ReenqueueRetryType getRetryType() {
        return retryType;
    }

    public void setRetryType(ReenqueueRetryType retryType) {
        this.retryType = retryType;
    }
}
