package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.TargetType;
import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.CertificateProgressService;
import com.lms.www.management.service.CertificateService;
import com.lms.www.management.service.StudentBatchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentBatchServiceImpl implements StudentBatchService {

    private final StudentBatchRepository studentBatchRepository;
    private final BatchRepository batchRepository;
    private final CertificateProgressService certificateProgressService;
    private final CertificateService certificateService;
    private final CourseRepository courseRepository;

    // ================= ENROLL =================
    @Override
    public StudentBatch enrollStudent(StudentBatch studentBatch) {

        Long studentId = studentBatch.getStudentId();
        Long courseId = studentBatch.getCourseId();
        Long batchId = studentBatch.getBatchId();

        // ❌ Same batch duplicate
        if (studentBatchRepository
                .existsByStudentIdAndBatchIdAndStatus(
                        studentId, batchId, "ACTIVE")) {
            throw new IllegalStateException(
                    "Student already active in this batch");
        }

        // ❌ Same course multiple active batches
        if (studentBatchRepository
                .existsByStudentIdAndCourseIdAndStatus(
                        studentId, courseId, "ACTIVE")) {
            throw new IllegalStateException(
                    "Student already has an active batch for this course");
        }

        // 🔹 Load batch
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found"));

        // 🔹 Count active students
        long activeCount =
                studentBatchRepository
                        .countByBatchIdAndStatus(batchId, "ACTIVE");

        // 🔹 Batch capacity check
        if (batch.getMaxStudents() != null
                && activeCount >= batch.getMaxStudents()) {

            throw new IllegalStateException(
                    "Batch is full. Please create a new batch.");
        }

        // 🔹 Enroll student
        studentBatch.setBatchId(batch.getBatchId());
        studentBatch.setStatus("ACTIVE");
        studentBatch.setJoinedAt(LocalDateTime.now());

        return studentBatchRepository.save(studentBatch);
    }

    // ================= UPDATE =================
    @Override
    public StudentBatch updateEnrollment(Long studentBatchId, StudentBatch updated) {

        StudentBatch existing = studentBatchRepository.findById(studentBatchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Enrollment not found"));

        if (updated.getStatus() != null) {

            String previousStatus = existing.getStatus();
            String newStatus = updated.getStatus();

            // Update status first
            existing.setStatus(newStatus);
            studentBatchRepository.save(existing);

            // 🎓 Trigger certificate only when ACTIVE → COMPLETED
            if ("COMPLETED".equalsIgnoreCase(newStatus)
                    && !"COMPLETED".equalsIgnoreCase(previousStatus)) {

                Long studentId = existing.getStudentId();
                Long courseId = existing.getCourseId();

                // Fetch course
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Course not found"));

                // Generate certificate
                var certificate = certificateService.generateCertificateIfEligible(
                        studentId,
                        TargetType.COURSE,
                        courseId,
                        existing.getStudentName(),
                        existing.getStudentEmail(),
                        course.getCourseName(),
                        null
                );

                if (certificate == null) {
                    System.out.println("Student completed course but certificate was not generated due to eligibility rules.");
                }
            }
        }

        return existing;
    }

    @Override
    public List<StudentBatch> getStudentsByBatch(Long batchId) {
        return studentBatchRepository.findByBatchId(batchId);
    }

    // ================= VIEW OWN =================
    @Override
    public StudentBatch getStudentCurrentBatch(Long studentId) {
        return studentBatchRepository
                .findFirstByStudentIdAndStatus(studentId, "ACTIVE")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Active batch not found"));
    }

    // ================= REMOVE =================
    @Override
    public void removeStudent(Long studentBatchId) {
        studentBatchRepository.deleteById(studentBatchId);
    }

    // ================= BULK ENROLL =================
    @Override
    public int bulkEnroll(Long courseId,
                          Long batchId,
                          List<StudentBatch> students) {

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found"));

        long activeCount =
                studentBatchRepository.countByBatchIdAndStatus(batchId, "ACTIVE");

        int enrolledCount = 0;

        for (StudentBatch s : students) {

            // Capacity check
            if (batch.getMaxStudents() != null
                    && activeCount >= batch.getMaxStudents()) {
                break;
            }

            // Skip duplicate active enrollment
            if (studentBatchRepository
                    .existsByStudentIdAndCourseIdAndStatus(
                            s.getStudentId(), courseId, "ACTIVE")) {
                continue;
            }

            StudentBatch sb = new StudentBatch();
            sb.setStudentId(s.getStudentId());
            sb.setStudentName(s.getStudentName());
            sb.setStudentEmail(s.getStudentEmail());
            sb.setCourseId(courseId);
            sb.setBatchId(batchId);
            sb.setStatus("ACTIVE");
            sb.setJoinedAt(LocalDateTime.now());

            studentBatchRepository.save(sb);

            activeCount++;
            enrolledCount++;
        }

        return enrolledCount;
    }

    @Override
    public List<StudentBatch> getAllEnrollments() {
        return studentBatchRepository.findAll();
    }
}