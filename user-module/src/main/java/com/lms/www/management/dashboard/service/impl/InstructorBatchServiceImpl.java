package com.lms.www.management.dashboard.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.lms.www.management.dashboard.dto.StudentInfoDTO;
import com.lms.www.management.dashboard.service.InstructorBatchService;
import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.instructor.dto.InstructorBatchDTO;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.model.Course;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.StudentBatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorBatchServiceImpl implements InstructorBatchService {

    private final BatchRepository batchRepository;
    private final StudentBatchRepository studentBatchRepository;
    private final CourseRepository courseRepository;

    @Override
    public Page<InstructorBatchDTO> getAssignedBatches(Long instructorId, Pageable pageable) {
        return batchRepository.findByTrainerId(instructorId, pageable)
                .map(this::mapToInstructorBatchDTO);
    }

    @Override
    public Page<StudentInfoDTO> getStudentsInBatch(Long instructorId, Long batchId, Pageable pageable) {
        validateBatchOwnership(instructorId, batchId);

        return studentBatchRepository
                .findByBatchId(batchId, pageable)
                .map(this::mapToStudentInfoDTO);
    }

    private void validateBatchOwnership(Long instructorId, Long batchId) {
        if (instructorId == null) {
            throw new AccessDeniedException("User identity not found. Please log in again.");
        }
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        if (!java.util.Objects.equals(instructorId, batch.getTrainerId())) {
            throw new AccessDeniedException("Unauthorized to access this batch");
        }
    }

    private InstructorBatchDTO mapToInstructorBatchDTO(Batch batch) {

        long studentsCount = studentBatchRepository.countByBatchIdAndStatus(batch.getBatchId(), "ACTIVE");

        String courseName = null;
        if (batch.getCourseId() != null) {
            courseName = courseRepository.findById(batch.getCourseId())
                    .map(Course::getCourseName)
                    .orElse(null);
        }

        return InstructorBatchDTO.builder()
                .batchId(batch.getBatchId())
                .batchName(batch.getBatchName())
                .startDate(batch.getStartDate())
                .endDate(batch.getEndDate())
                .studentsCount((int) studentsCount)
                .courseName(courseName)
                .courseId(batch.getCourseId())
                .build();
    }

    private StudentInfoDTO mapToStudentInfoDTO(StudentBatch studentBatch) {
        return StudentInfoDTO.builder()
                .studentId(studentBatch.getStudentId())
                .studentName(studentBatch.getStudentName())
                .email(studentBatch.getStudentEmail())
                .phone(null) // Phone not available in StudentBatch directly
                .status(studentBatch.getStatus())
                .build();
    }
}
