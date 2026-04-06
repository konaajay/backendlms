package com.lms.www.campus.Hostel.Transport;

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

    public StudentTransportAssignment() {
    }

    public StudentTransportAssignment(Long id, Long studentId, Vehicle vehicle, RouteWay route, String pickupPoint,
            String dropPoint, Shift shift) {
        this.id = id;
        this.studentId = studentId;
        this.vehicle = vehicle;
        this.route = route;
        this.pickupPoint = pickupPoint;
        this.dropPoint = dropPoint;
        this.shift = shift;
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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public RouteWay getRoute() {
        return route;
    }

    public void setRoute(RouteWay route) {
        this.route = route;
    }

    public String getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public String getDropPoint() {
        return dropPoint;
    }

    public void setDropPoint(String dropPoint) {
        this.dropPoint = dropPoint;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
