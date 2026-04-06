package com.lms.www.campus.repository.Transport;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.StudentTransportAssignment;

@Repository
public interface StudentTransportAssignmentRepository
        extends JpaRepository<StudentTransportAssignment, Long> {

    List<StudentTransportAssignment> findByStudentId(Long studentId);

    List<StudentTransportAssignment> findByVehicle_Id(Long vehicleId);

    Optional<StudentTransportAssignment> findByStudentIdAndShift(Long studentId, String shift);
}
