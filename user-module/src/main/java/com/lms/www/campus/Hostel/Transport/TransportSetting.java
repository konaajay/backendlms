package com.lms.www.campus.Hostel.Transport;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
public class TransportSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_name", unique = true)
    private String keyName;

    private String value;

    public TransportSetting() {}

    public TransportSetting(Long id, String keyName, String value) {
        this.id = id;
        this.keyName = keyName;
        this.value = value;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}