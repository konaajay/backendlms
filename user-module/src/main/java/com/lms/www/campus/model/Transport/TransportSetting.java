package com.lms.www.campus.model.Transport;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
@Data
public class TransportSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_name", unique = true)
    private String keyName;

    private String value;
}