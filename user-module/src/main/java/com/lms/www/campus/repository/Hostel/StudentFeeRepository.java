package com.lms.www.campus.repository.Hostel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.StudentHostelFee;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentHostelFee, Long> {

    List<StudentHostelFee> findByStudentId(Long studentId);

    List<StudentHostelFee> findByStatus(StudentHostelFee.FeeStatus status);
}
