package com.lms.www.campus.model.Transport;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

// REDUNDANT - MOVED TO com.lms.www.campus.Transport
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteWay

{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;;

    @Column(name = "route_Code")
    private Long routeCode;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @ElementCollection
    @CollectionTable(name = "route_pickup_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "pickup_point")
    private List<String> pickupPoints;

    @ElementCollection
    @CollectionTable(name = "route_drop_points", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "drop_point")
    private List<String> dropPoints;

    private Double distanceKm;

    private Integer estimatedTimeMinutes;

    /* =============VEHICLES ASSIGNED TO THIS ROUTE ========= */

    @OneToMany(mappedBy = "route")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("route")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Vehicle> vehicles;

    @Column(nullable = false)
    private Boolean active = true;

}
