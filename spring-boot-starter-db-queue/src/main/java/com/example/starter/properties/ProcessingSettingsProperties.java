package com.example.starter.properties;

import com.example.dbqueue.settings.ProcessingMode;

public class ProcessingSettingsProperties {

    private ProcessingMode processingMode = ProcessingMode.SEPARATE_TRANSACTIONS;
    private Integer threadCount = 1;

    public ProcessingSettingsProperties(ProcessingMode processingMode, Integer threadCount) {
        this.processingMode = processingMode;
        this.threadCount = threadCount;
    }

    public ProcessingSettingsProperties() {
    }

    public ProcessingMode getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(ProcessingMode processingMode) {
        this.processingMode = processingMode;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }
}
