package com.lms.www.campus.model.Transport;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
@Data
public class FuelLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private Long odo;

    @Column(nullable = false)
    private String station;
}