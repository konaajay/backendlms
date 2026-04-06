package com.lms.www.management.dashboard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lms.www.management.dashboard.dto.StudentInfoDTO;
import com.lms.www.management.instructor.dto.InstructorBatchDTO;

public interface InstructorBatchService {
    Page<InstructorBatchDTO> getAssignedBatches(Long instructorId, Pageable pageable);

    Page<StudentInfoDTO> getStudentsInBatch(Long instructorId, Long batchId, Pageable pageable);
}
