package com.lms.www.campus.repository.Transport;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.DriverDetails;
import com.lms.www.campus.Transport.DriverDetails.DriverStatus;
import com.lms.www.campus.Transport.DriverDetails.Role;

@Repository
public interface DriverDetailsRepository extends JpaRepository<DriverDetails, Long> {

    Optional<DriverDetails> findByLicenseNumber(String licenseNumber);

    List<DriverDetails> findByActiveTrue();

    List<DriverDetails> findByVerificationStatus(DriverStatus status);

    List<DriverDetails> findByRole(Role role);

    List<DriverDetails> findByVehicle_VehicleNumber(String vehicleNumber);

    List<DriverDetails> findByRoute_Id(Long routeId);

    List<DriverDetails> findByVehicle(com.lms.www.campus.Transport.Vehicle vehicle);

}
