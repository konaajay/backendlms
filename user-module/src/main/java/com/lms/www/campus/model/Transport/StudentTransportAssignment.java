package com.lms.www.campus.model.Transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
public class StudentTransportAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({ "route", "hibernateLazyInitializer", "handler" })
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    @JsonIgnoreProperties({ "vehicles", "hibernateLazyInitializer", "handler" })
    private RouteWay route;

    @Column(nullable = false, length = 100)
    private String pickupPoint;

    @Column(nullable = false, length = 100)
    private String dropPoint;

    @Enumerated(EnumType.STRING)
    private Shift shift;

    public enum Shift {
        Morning,
        Evening,
        Both
    }
}
