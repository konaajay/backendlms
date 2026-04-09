package com.lms.www.campus.Transport;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "route_way")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RouteWay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "route_code")
    private Long routeCode;

    @Column(name = "route_name", nullable = false)
    private String routeName;
    
    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "route_pickup_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "pickup_point")
    private List<String> pickupPoints = new ArrayList<>();

    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "route_drop_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "drop_point")
    private List<String> dropPoints = new ArrayList<>();
    
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;
    
    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Vehicle> vehicles = new ArrayList<>();

    @JsonProperty("vehicleCount")
    public int getVehicleCount() {
        return vehicles != null ? vehicles.size() : 0;
    }
    
    @Column(nullable = false)
    private Boolean active = true;

    public RouteWay() {}

    public RouteWay(Long id, Long routeCode, String routeName, List<String> pickupPoints, List<String> dropPoints, Double distanceKm, Integer estimatedTimeMinutes, List<Vehicle> vehicles, Boolean active) {
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteCode() { return routeCode; }
    public void setRouteCode(Long routeCode) { this.routeCode = routeCode; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public List<String> getPickupPoints() { return pickupPoints; }
    public void setPickupPoints(List<String> pickupPoints) { this.pickupPoints = pickupPoints; }

    public List<String> getDropPoints() { return dropPoints; }
    public void setDropPoints(List<String> dropPoints) { this.dropPoints = dropPoints; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }

    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
