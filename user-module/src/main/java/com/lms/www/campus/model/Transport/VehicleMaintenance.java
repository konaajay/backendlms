package com.lms.www.campus.model.Transport;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
public class VehicleMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vehicle Number (TS 09 A 2834)
    @Column(nullable = false)
    private String vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double cost;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    private LocalDate nextDue;

    /* ================= ENUMS ================= */

    public enum MaintenanceType {
        Service,
        Repair,
        Inspection
    }

    public enum MaintenanceStatus {
        Pending,
        InProgress,
        Completed
    }
}
