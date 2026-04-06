package com.lms.www.campus.repository.Transport;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.ConductorDetails;
import com.lms.www.campus.Transport.Vehicle;
import com.lms.www.campus.Transport.ConductorDetails.ConductorVerificationStatus;

@Repository
public interface ConductorDetailsRepository
                extends JpaRepository<ConductorDetails, Long> {

        List<ConductorDetails> findByActiveTrue();

        List<ConductorDetails> findByVerificationStatus(
                        ConductorVerificationStatus status);

        List<ConductorDetails> findByVehicle_VehicleNumber(String vehicleNumber);

        List<ConductorDetails> findByRoute_Id(Long routeId);

        List<ConductorDetails> findByVehicle(Vehicle vehicle);
}