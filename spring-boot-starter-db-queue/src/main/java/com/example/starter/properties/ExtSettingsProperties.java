package com.example.starter.properties;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExtSettingsProperties {

    private Map<String, String> settings = new LinkedHashMap<>();

    public ExtSettingsProperties(Map<String, String> settings) {
        this.settings = settings;
    }

    public ExtSettingsProperties() {
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
