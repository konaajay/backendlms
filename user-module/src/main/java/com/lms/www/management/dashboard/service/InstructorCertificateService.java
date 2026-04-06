package com.lms.www.management.dashboard.service;

import com.lms.www.management.instructor.dto.InstructorCertificateRequestDTO;
import com.lms.www.management.model.Certificate;

public interface InstructorCertificateService {
    Certificate generateCertificateForStudent(Long instructorId, Long studentId,
            InstructorCertificateRequestDTO request);
}
