package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamSchedule;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {

    List<ExamSchedule> findByExamIdAndIsActiveTrue(Long examId);

    List<ExamSchedule> findByCourseIdAndIsActiveTrue(Long courseId);

    List<ExamSchedule> findByBatchIdAndIsActiveTrue(Long batchId);

    Optional<ExamSchedule> findByScheduleIdAndIsActiveTrue(Long scheduleId);
    
    List<ExamSchedule> findByIsActiveFalse();
    
    List<ExamSchedule> findByBatchIdInAndIsActiveTrue(List<Long> batchIds);
}
