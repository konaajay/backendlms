package com.lms.www.campus.Hostel.Transport;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
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

    public enum MarkedBy {
        MANUAL, QRCODE
    }

    public enum PickupStatus {
        PICKED_UP, DROPPED, ABSENT, SKIPPED
    }

    public enum DropStatus {
        DROPPED, ABSENT, SKIPPED
    }

    public TransportAttendance() {
    }

    public TransportAttendance(Long id, Long studentId, LocalDate attendanceDate, MarkedBy markedBy, RouteWay route,
            Vehicle vehicle, PickupStatus pickupStatus, DropStatus dropStatus) {
        this.id = id;
        this.studentId = studentId;
        this.attendanceDate = attendanceDate;
        this.markedBy = markedBy;
        this.route = route;
        this.vehicle = vehicle;
        this.pickupStatus = pickupStatus;
        this.dropStatus = dropStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public MarkedBy getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(MarkedBy markedBy) {
        this.markedBy = markedBy;
    }

    public RouteWay getRoute() {
        return route;
    }

    public void setRoute(RouteWay route) {
        this.route = route;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public PickupStatus getPickupStatus() {
        return pickupStatus;
    }

    public void setPickupStatus(PickupStatus pickupStatus) {
        this.pickupStatus = pickupStatus;
    }

    public DropStatus getDropStatus() {
        return dropStatus;
    }

    public void setDropStatus(DropStatus dropStatus) {
        this.dropStatus = dropStatus;
    }
}
