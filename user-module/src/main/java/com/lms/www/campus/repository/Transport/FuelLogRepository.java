package com.lms.www.campus.repository.Transport;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.FuelLog;

@Repository
public interface FuelLogRepository extends JpaRepository<FuelLog, Long> {

    List<FuelLog> findByVehicleId(String vehicleId);

    List<FuelLog> findByDate(LocalDate date);

    List<FuelLog> findByVehicleIdAndDate(String vehicleId, LocalDate date);
}
