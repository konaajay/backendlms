package com.lms.www.campus.model.Transport;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicletype;

    public enum VehicleType {
        BUS, VAN, CAB
    }

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "occupiedSeats")
    private Integer occupiedSeats;

    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    public enum VehicleStatus {
        ACTIVE,
        MAINTENANCE,
        INACTIVE
    }

    @CreationTimestamp
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    /* ================= ROUTE ASSIGNMENT USING ROUTE CODE ================= */

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RouteWay route;

    private Boolean gpsEnabled;

}
