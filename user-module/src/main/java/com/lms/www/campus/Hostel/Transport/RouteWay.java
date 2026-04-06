package com.lms.www.campus.Hostel.Transport;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
public class RouteWay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_Code")
    private Long routeCode;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @ElementCollection
    @CollectionTable(name = "route_pickup_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "pickup_point")
    private List<String> pickupPoints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "route_drop_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "drop_point")
    private List<String> dropPoints = new ArrayList<>();

    private Double distanceKm;

    private Integer estimatedTimeMinutes;

    @OneToMany(mappedBy = "route")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("route")
    private List<Vehicle> vehicles = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    public RouteWay() {
    }

    public RouteWay(Long id, Long routeCode, String routeName, List<String> pickupPoints, List<String> dropPoints,
            Double distanceKm, Integer estimatedTimeMinutes, List<Vehicle> vehicles, Boolean active) {
        this.id = id;
        this.routeCode = routeCode;
        this.routeName = routeName;
        this.pickupPoints = pickupPoints;
        this.dropPoints = dropPoints;
        this.distanceKm = distanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.vehicles = vehicles;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(Long routeCode) {
        this.routeCode = routeCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<String> getPickupPoints() {
        return pickupPoints;
    }

    public void setPickupPoints(List<String> pickupPoints) {
        this.pickupPoints = pickupPoints;
    }

    public List<String> getDropPoints() {
        return dropPoints;
    }

    public void setDropPoints(List<String> dropPoints) {
        this.dropPoints = dropPoints;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
