package com.lms.www.fee.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "global_configs")
public class GlobalConfig {

    @Id
    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;

    @Column(name = "config_value")
    private String configValue;

    @Column(name = "description")
    private String description;

    public GlobalConfig() {}

    public GlobalConfig(String configKey, String configValue, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
    }

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String key) { this.configKey = key; }
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String val) { this.configValue = val; }
    public String getDescription() { return description; }
    public void setDescription(String desc) { this.description = desc; }
}
