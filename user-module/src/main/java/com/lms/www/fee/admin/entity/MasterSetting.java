package com.lms.www.fee.admin.entity;

import com.lms.www.fee.enums.MasterSettingType;
import jakarta.persistence.*;

@Entity
@Table(name = "master_settings")
public class MasterSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MasterSettingType type;

    @Column(name = "key_name", nullable = false)
    private String keyName;

    @Column(name = "setting_value", nullable = false)
    private String value;

    @Column(name = "description")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MasterSettingType getType() { return type; }
    public void setType(MasterSettingType type) { this.type = type; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public static MasterSettingBuilder builder() {
        return new MasterSettingBuilder();
    }

    public static class MasterSettingBuilder {
        private MasterSetting setting = new MasterSetting();
        public MasterSettingBuilder type(MasterSettingType type) { setting.type = type; return this; }
        public MasterSettingBuilder keyName(String key) { setting.keyName = key; return this; }
        public MasterSettingBuilder value(String value) { setting.value = value; return this; }
        public MasterSettingBuilder description(String desc) { setting.description = desc; return this; }
        public MasterSettingBuilder active(boolean active) { setting.active = active; return this; }
        public MasterSetting build() { return setting; }
    }
}
