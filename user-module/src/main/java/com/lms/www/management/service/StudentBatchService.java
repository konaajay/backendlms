package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.StudentBatch;

public interface StudentBatchService {

    StudentBatch enrollStudent(StudentBatch studentBatch);

    StudentBatch updateEnrollment(Long studentBatchId, StudentBatch updated);

    List<StudentBatch> getStudentsByBatch(Long batchId);

    StudentBatch getStudentCurrentBatch(Long studentId);

    void removeStudent(Long studentBatchId);
    
    int bulkEnroll(Long courseId, Long batchId, List<StudentBatch> students);
}
