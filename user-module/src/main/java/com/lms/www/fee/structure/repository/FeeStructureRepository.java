package com.lms.www.fee.structure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.www.fee.structure.entity.FeeStructure;
import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

	Optional<FeeStructure> findByBatchIdAndAcademicYearAndActiveTrue(Long batchId, String academicYear);

	Optional<FeeStructure> findByCourseIdAndAcademicYearAndActiveTrue(Long courseId, String academicYear);

	boolean existsByBatchIdAndActiveTrue(Long batchId);

	List<FeeStructure> findByBatchId(Long batchId);

	List<FeeStructure> findByBatchIdAndIsActiveTrue(Long batchId);

	List<FeeStructure> findByCourseId(Long courseId);

	List<FeeStructure> findByCourseIdAndBatchIdIsNullAndIsActiveTrue(Long courseId);

}
