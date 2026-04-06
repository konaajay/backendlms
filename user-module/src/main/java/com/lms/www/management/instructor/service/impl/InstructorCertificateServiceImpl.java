package com.lms.www.management.instructor.service.impl;

import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.dashboard.service.InstructorCertificateService;
import com.lms.www.management.instructor.dto.InstructorCertificateRequestDTO;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorCertificateServiceImpl implements InstructorCertificateService {

    private final StudentBatchRepository studentBatchRepository;
    private final CertificateService certificateService;

    @Override
    @Transactional
    public Certificate generateCertificateForStudent(Long instructorId, Long studentId,
            InstructorCertificateRequestDTO request) {

        Optional<StudentBatch> batchOpt = studentBatchRepository.findByStudentIdAndTrainerId(studentId, instructorId);

        if (batchOpt.isEmpty()) {
            throw new AccessDeniedException("Unauthorized to generate certificate for this student");
        }

        StudentBatch studentBatch = batchOpt.get(); // Any active valid batch linking them works

        return certificateService.generateCertificateIfEligible(
                studentId,
                request.getTargetType(),
                request.getTargetId(),
                studentBatch.getStudentName(),
                studentBatch.getStudentEmail(),
                request.getEventTitle(),
                request.getScore() != null ? request.getScore() : 0.0);
    }
}
