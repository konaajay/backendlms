package com.lms.www.campus.Hostel.Transport;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TransportFeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id", nullable = false, unique = true)
    private Long routeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal annualFee;

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    public TransportFeeStructure() {
    }

    public TransportFeeStructure(Long id, Long routeId, BigDecimal annualFee, String academicYear) {
        this.id = id;
        this.routeId = routeId;
        this.annualFee = annualFee;
        this.academicYear = academicYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}