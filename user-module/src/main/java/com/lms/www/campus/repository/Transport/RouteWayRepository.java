package com.lms.www.campus.repository.Transport;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.RouteWay;

@Repository
public interface RouteWayRepository extends JpaRepository<RouteWay, Long> {

    // Get only active routes
    List<RouteWay> findByActiveTrue();

    // Get routes assigned to a vehicle
    List<RouteWay> findByVehicles_VehicleNumber(String vehicleNumber);

    Optional<RouteWay> findByRouteCode(Long routeCode);

    boolean existsByRouteCode(Long routeCode);
}