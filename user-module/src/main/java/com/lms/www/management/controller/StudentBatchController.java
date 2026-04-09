package com.lms.www.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.CertificateRepository;
import com.lms.www.management.service.StudentBatchService;
import com.lms.www.management.util.SecurityUtil;
import com.lms.www.fee.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student-batches")
@RequiredArgsConstructor
public class StudentBatchController {

        private final StudentBatchService studentBatchService;
        private final CertificateRepository certificateRepository;
        private final SecurityUtil securityUtil;

        // ================= ENROLL =================
        @PostMapping("/enroll")
        @PreAuthorize("hasAuthority('STUDENT_BATCH_CREATE') or hasAuthority('ALL_PERMISSIONS')")
        public ResponseEntity<StudentBatch> enrollStudent(
                        @RequestBody StudentBatch studentBatch) {

                return new ResponseEntity<>(
                                studentBatchService.enrollStudent(studentBatch),
                                HttpStatus.CREATED);
        }

        // ================= VIEW BY BATCH =================
        @GetMapping("/batch/{batchId}")
        @PreAuthorize("hasAuthority('STUDENT_BATCH_VIEW') or hasAuthority('ALL_PERMISSIONS')")
        public ResponseEntity<List<StudentBatch>> getStudentsByBatch(
                        @PathVariable Long batchId) {

                return ResponseEntity.ok(
                                studentBatchService.getStudentsByBatch(batchId));
        }

        // ================= VIEW OWN =================
        @GetMapping("/student/{studentId}")
        @PreAuthorize("hasAuthority('STUDENT_BATCH_SELF_VIEW') or hasAuthority('ALL_PERMISSIONS')")
        public ResponseEntity<ApiResponse<StudentBatch>> getStudentBatch(
                        @PathVariable Long studentId) {

                StudentBatch batch = studentBatchService.getStudentCurrentBatch(studentId);
                return ResponseEntity.ok(ApiResponse.success(batch));
        }

        // ================= GET MY ENROLLMENT (SESSION BASED) =================
        @GetMapping("/my-enrollment")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<StudentBatch>> getMyBatchEnrollment() {
                Long studentId = securityUtil.getUserId();
                StudentBatch batch = studentBatchService.getStudentCurrentBatch(studentId);
                return ResponseEntity.ok(ApiResponse.success(batch));
        }

        // ================= UPDATE =================
        @PutMapping("/{studentBatchId}")
        @PreAuthorize("hasAuthority('STUDENT_BATCH_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
        public ResponseEntity<Map<String, Object>> updateEnrollment(
                        @PathVariable Long studentBatchId,
                        @RequestBody StudentBatch updated) {

                StudentBatch result = studentBatchService.updateEnrollment(studentBatchId, updated);

                boolean certificateGenerated = certificateRepository.existsByUserIdAndTargetTypeAndTargetId(
                                result.getStudentId(),
                                com.lms.www.management.enums.TargetType.COURSE,
                                result.getCourseId());

                Map<String, Object> response = new HashMap<>();

                // Original fields
                response.put("studentBatchId", result.getStudentBatchId());
                response.put("studentId", result.getStudentId());
                response.put("studentName", result.getStudentName());
                response.put("studentEmail", result.getStudentEmail());
                response.put("courseId", result.getCourseId());
                response.put("batchId", result.getBatchId());
                response.put("joinedAt", result.getJoinedAt());
                response.put("status", result.getStatus());

                // Certificate info
                response.put("certificateGenerated", certificateGenerated);

                if (!certificateGenerated) {
                        response.put(
                                        "reason",
                                        "Student completed course but is not eligible for certificate");
                }

                return ResponseEntity.ok(response);
        }

        // ================= BULK ENROLL =================
        @PostMapping("/bulk-enroll")
        @PreAuthorize("hasAuthority('STUDENT_BATCH_CREATE') or hasAuthority('ALL_PERMISSIONS')")
        public ResponseEntity<?> bulkEnroll(
                        @RequestBody Map<String, Object> request) {

                Long courseId = Long.valueOf(request.get("courseId").toString());
                Long batchId = Long.valueOf(request.get("batchId").toString());

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> studentMaps = (List<Map<String, Object>>) request.get("students");

                List<StudentBatch> students = studentMaps.stream().map(m -> {

                        if (m.get("studentEmail") == null) {
                                throw new IllegalArgumentException("Student email is required");
                        }

                        StudentBatch sb = new StudentBatch();
                        sb.setStudentId(Long.valueOf(m.get("studentId").toString()));
                        sb.setStudentName(m.get("studentName").toString());
                        sb.setStudentEmail(m.get("studentEmail").toString());

                        return sb;

                }).toList();

                int count = studentBatchService.bulkEnroll(courseId, batchId, students);

                return ResponseEntity.ok(
                                Map.of(
                                                "message", "Bulk enrollment completed",
                                                "enrolledCount", count));
        }
    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_BATCH_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<StudentBatch>> getAllEnrollments() {
        return ResponseEntity.ok(studentBatchService.getAllEnrollments());
    }
}