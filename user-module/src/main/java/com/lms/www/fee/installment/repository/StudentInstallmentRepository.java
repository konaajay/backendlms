package com.lms.www.fee.installment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.www.fee.installment.entity.StudentInstallment;
import java.util.List;

public interface StudentInstallmentRepository extends JpaRepository<StudentInstallment, Long> {
    List<StudentInstallment> findByStudentFeeId(Long studentFeeId);
}
