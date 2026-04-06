package com.lms.www.campus.repository.Transport;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.VehicleGPS;

@Repository
public interface VehicleGPSRepository
                extends JpaRepository<VehicleGPS, Long> {
        // Full history
        List<VehicleGPS> findByVehicle_IdOrderByTimestampDesc(Long vehicleId);

        // Latest record
        Optional<VehicleGPS> findTopByVehicle_IdOrderByTimestampDesc(Long vehicleId);

        // By vehicle number
        List<VehicleGPS> findByVehicle_VehicleNumber(String vehicleNumber);
}
