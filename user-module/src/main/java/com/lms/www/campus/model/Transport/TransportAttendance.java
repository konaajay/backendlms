package com.lms.www.campus.model.Transport;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
@Data
public class TransportAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarkedBy markedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private RouteWay route;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PickupStatus pickupStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DropStatus dropStatus;

    /* ================= ENUMS ================= */

    public enum MarkedBy {
        MANUAL, QRCODE
    }

    public enum PickupStatus {
        PICKED_UP, DROPPED, ABSENT, SKIPPED
    }

    public enum DropStatus {
        DROPPED, ABSENT, SKIPPED
    }
}
