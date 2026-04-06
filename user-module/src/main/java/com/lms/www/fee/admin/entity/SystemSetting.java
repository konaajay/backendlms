package com.lms.www.fee.admin.entity;

import com.lms.www.fee.enums.MasterSettingType;
import jakarta.persistence.*;

@Entity
@Table(name = "fee_system_settings")
public class SystemSetting {

    @Id
    @Column(name = "setting_key", length = 100)
    private String key;

    @Column(name = "setting_value", nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "setting_type")
    private MasterSettingType type = MasterSettingType.SYSTEM;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private boolean active = true;

    public SystemSetting() {}

    public static SystemSettingBuilder builder() {
        return new SystemSettingBuilder();
    }

    public static class SystemSettingBuilder {
        private SystemSetting setting = new SystemSetting();
        public SystemSettingBuilder key(String key) { setting.key = key; return this; }
        public SystemSettingBuilder value(String value) { setting.value = value; return this; }
        public SystemSettingBuilder type(MasterSettingType type) { setting.type = type; return this; }
        public SystemSettingBuilder description(String d) { setting.description = d; return this; }
        public SystemSettingBuilder active(boolean active) { setting.active = active; return this; }
        public SystemSetting build() { return setting; }
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public MasterSettingType getType() { return type; }
    public void setType(MasterSettingType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
