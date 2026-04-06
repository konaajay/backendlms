package com.lms.www.campus.Transport;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Vehicles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
        
    @Column(name = "vehicle_number", nullable = false, unique = true)
    private String vehicleNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicletype")
    private VehicleType vehicletype;
    
    public enum VehicleType {
        BUS, VAN, CAB
    }
    
    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "occupied_seats")
    private Integer occupiedSeats;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_status")
    private VehicleStatus vehicleStatus;
    
    public enum VehicleStatus {
        ACTIVE,
        MAINTENANCE,
        INACTIVE
    }
   
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = true)
    private RouteWay route;

    @Column(name = "gps_enabled")
    private Boolean gpsEnabled;

    public Vehicle() {}

    public Vehicle(Long id, String vehicleNumber, VehicleType vehicletype, Integer capacity, Integer occupiedSeats, VehicleStatus vehicleStatus, LocalDateTime createdDate, RouteWay route, Boolean gpsEnabled) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.vehicletype = vehicletype;
        this.capacity = capacity;
        this.occupiedSeats = occupiedSeats;
        this.vehicleStatus = vehicleStatus;
        this.createdDate = createdDate;
        this.route = route;
        this.gpsEnabled = gpsEnabled;
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public VehicleType getVehicletype() { return vehicletype; }
    public void setVehicletype(VehicleType vehicletype) { this.vehicletype = vehicletype; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getOccupiedSeats() { return occupiedSeats; }
    public void setOccupiedSeats(Integer occupiedSeats) { this.occupiedSeats = occupiedSeats; }

    public VehicleStatus getVehicleStatus() { return vehicleStatus; }
    public void setVehicleStatus(VehicleStatus vehicleStatus) { this.vehicleStatus = vehicleStatus; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public RouteWay getRoute() { return route; }
    public void setRoute(RouteWay route) { this.route = route; }

    public Boolean getGpsEnabled() { return gpsEnabled; }
    public void setGpsEnabled(Boolean gpsEnabled) { this.gpsEnabled = gpsEnabled; }
}
