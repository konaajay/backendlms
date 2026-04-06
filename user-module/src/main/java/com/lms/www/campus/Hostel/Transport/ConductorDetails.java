package com.lms.www.campus.Hostel.Transport;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
public class ConductorDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conductorId;

    @Column(nullable = false)
    private String conductorName;

    @Column(nullable = false, unique = true)
    private String contactNumber;

    @Column(nullable = false)
    private Integer experienceYears;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteWay route;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConductorVerificationStatus verificationStatus;

    public enum ConductorVerificationStatus {
        PENDING,
        VERIFIED,
        SUSPENDED,
        REJECTED
    }

    @Column(nullable = false)
    private Boolean active = true;

    public ConductorDetails() {
    }

    public ConductorDetails(Long conductorId, String conductorName, String contactNumber, Integer experienceYears,
            RouteWay route, Vehicle vehicle, ConductorVerificationStatus verificationStatus, Boolean active) {
        this.conductorId = conductorId;
        this.conductorName = conductorName;
        this.contactNumber = contactNumber;
        this.experienceYears = experienceYears;
        this.route = route;
        this.vehicle = vehicle;
        this.verificationStatus = verificationStatus;
        this.active = active;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public void setConductorId(Long conductorId) {
        this.conductorId = conductorId;
    }

    public String getConductorName() {
        return conductorName;
    }

    public void setConductorName(String conductorName) {
        this.conductorName = conductorName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
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

    public ConductorVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(ConductorVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
