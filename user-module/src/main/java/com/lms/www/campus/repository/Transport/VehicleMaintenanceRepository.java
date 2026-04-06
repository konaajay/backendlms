package com.lms.www.campus.repository.Transport;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.VehicleMaintenance;

@Repository
public interface VehicleMaintenanceRepository
        extends JpaRepository<VehicleMaintenance, Long> {

    List<VehicleMaintenance> findByVehicleId(String vehicleId);

    List<VehicleMaintenance> findByStatus(
            VehicleMaintenance.MaintenanceStatus status);

    List<VehicleMaintenance> findByNextDueBefore(LocalDate date);
}
