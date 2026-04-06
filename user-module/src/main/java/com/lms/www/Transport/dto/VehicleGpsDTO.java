package com.lms.www.Transport.dto;

import java.time.LocalDateTime;

public class VehicleGpsDTO {

    private Long id;
    private double latitude;
    private double longitude;
    private double speed;
    private String status;
    private LocalDateTime timestamp;
    private Long vehicleId;

    public VehicleGpsDTO() {}

    public VehicleGpsDTO(Long id, double latitude, double longitude, double speed, String status, LocalDateTime timestamp, Long vehicleId) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.status = status;
        this.timestamp = timestamp;
        this.vehicleId = vehicleId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
}