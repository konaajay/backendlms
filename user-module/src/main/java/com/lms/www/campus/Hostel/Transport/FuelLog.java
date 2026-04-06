package com.lms.www.campus.Hostel.Transport;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
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

    public FuelLog() {}

    public FuelLog(Long id, String vehicleId, LocalDate date, Double quantity, Double cost, Long odo, String station) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.date = date;
        this.quantity = quantity;
        this.cost = cost;
        this.odo = odo;
        this.station = station;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public Long getOdo() { return odo; }
    public void setOdo(Long odo) { this.odo = odo; }

    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }
}