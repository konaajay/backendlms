package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.StudentBatchTransfer;

public interface StudentBatchTransferService {

    StudentBatchTransfer transferStudent(
            Long studentId,
            Long courseId,
            Long toBatchId,
            String reason,
            String transferredBy
    );

    List<StudentBatchTransfer> getTransfersByStudent(Long studentId);

    List<StudentBatchTransfer> getTransfersByCourse(Long courseId);
}
