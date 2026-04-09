package com.lms.www.management.dashboard.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.lms.www.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.instructor.dto.InstructorBatchDTO;
import com.lms.www.management.instructor.dto.InstructorEvaluationRequestDTO;
import com.lms.www.management.instructor.dto.InstructorAttendanceRequestDTO;
import com.lms.www.management.instructor.dto.InstructorCertificateRequestDTO;
import com.lms.www.management.instructor.dto.PendingEvaluationProjection;
import com.lms.www.management.dashboard.dto.StudentInfoDTO;
import com.lms.www.management.dashboard.service.InstructorAttendanceService;
import com.lms.www.management.dashboard.service.InstructorBatchService;
import com.lms.www.management.dashboard.service.InstructorCertificateService;
import com.lms.www.management.dashboard.service.InstructorExamService;
import com.lms.www.management.dashboard.service.InstructorSessionService;
import com.lms.www.management.dashboard.service.InstructorWebinarService;
import com.lms.www.management.model.AttendanceRecord;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.Webinar;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorDashboardController {

    private final SecurityUtil securityUtil;
    private final InstructorBatchService batchService;
    private final InstructorSessionService sessionService;
    private final InstructorAttendanceService attendanceService;
    private final InstructorExamService examService;
    private final InstructorWebinarService webinarService;
    private final InstructorCertificateService certificateService;

    // ==========================================
 // ==========================================
 // 1. BATCHES
 // ==========================================
    @GetMapping("/batches")
    @PreAuthorize("hasAuthority('BATCH_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<?> getAssignedBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long instructorId = securityUtil.getInstructorId();
            if (instructorId == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Unable to resolve instructor identity. Please log in again.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(errorResponse);
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<InstructorBatchDTO> batchPage = batchService.getAssignedBatches(instructorId, pageable);
            return ResponseEntity.ok(batchPage.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

 @GetMapping("/batches/{batchId}/students")
 @PreAuthorize("hasAuthority('STUDENT_BATCH_VIEW') or hasAuthority('ALL_PERMISSIONS')")
 public ResponseEntity<List<StudentInfoDTO>> getStudentsInBatch(
         @PathVariable Long batchId,
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "10") int size) {

     Long instructorId = securityUtil.getInstructorId();

     Page<StudentInfoDTO> studentPage =
             batchService.getStudentsInBatch(instructorId, batchId, PageRequest.of(page, size));

     return ResponseEntity.ok(studentPage.getContent());
 }
    // ==========================================
    // 2. SESSIONS
    // ==========================================
    @PostMapping("/sessions/batch/{batchId}")
    @PreAuthorize("hasAuthority('SESSION_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Session> createSession(
            @PathVariable Long batchId,
            @RequestBody Session session) {
        Long instructorId = securityUtil.getInstructorId();
        return new ResponseEntity<>(
                sessionService.createSession(instructorId, batchId, session),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('SESSION_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Session> updateSession(
            @PathVariable Long sessionId,
            @RequestBody Session session) {
        Long instructorId = securityUtil.getInstructorId();
        return ResponseEntity.ok(sessionService.updateSession(instructorId, sessionId, session));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('SESSION_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        Long instructorId = securityUtil.getInstructorId();
        sessionService.deleteSession(instructorId, sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Session> getSessionById(@PathVariable Long sessionId) {
        Long instructorId = securityUtil.getInstructorId();
        return ResponseEntity.ok(sessionService.getSessionById(instructorId, sessionId));
    }

    @GetMapping("/sessions/batch/{batchId}")
    @PreAuthorize("hasAuthority('SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<Session>> getSessionsByBatchId(@PathVariable Long batchId) {
        Long instructorId = securityUtil.getInstructorId();
        return ResponseEntity.ok(sessionService.getSessionsByBatchId(instructorId, batchId));
    }

    // ==========================================
    // 3. ATTENDANCE
    // ==========================================
    @GetMapping("/attendance")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AttendanceRecord>> getAttendance(
            @RequestParam Long batchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long instructorId = securityUtil.getInstructorId();
        return ResponseEntity.ok(attendanceService.getAttendanceByBatchAndDate(instructorId, batchId, date));
    }

    @PostMapping("/attendance")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_CREATE') or hasAuthority('ATTENDANCE_RECORD_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AttendanceRecord> markAttendance(
            @RequestBody InstructorAttendanceRequestDTO request) {
        Long instructorId = securityUtil.getInstructorId();
        return ResponseEntity.ok(attendanceService.markStudentAttendance(instructorId, request));
    }

    // ==========================================
    // 4. EXAMS
    // ==========================================
    @GetMapping("/exams")
    @PreAuthorize("hasAuthority('EXAM_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Page<Exam>> getMyExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(examService.getInstructorExams(securityUtil.getInstructorId(), PageRequest.of(page, size)));
    }

    @PostMapping("/exams")
    @PreAuthorize("hasAuthority('EXAM_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        return new ResponseEntity<>(examService.createExam(securityUtil.getInstructorId(), exam), HttpStatus.CREATED);
    }

    @PutMapping("/exams/{examId}")
    @PreAuthorize("hasAuthority('EXAM_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Exam> updateExam(@PathVariable Long examId, @RequestBody Exam exam) {
        return ResponseEntity.ok(examService.updateExam(securityUtil.getInstructorId(), examId, exam));
    }

    @PutMapping("/exams/{examId}/publish")
    @PreAuthorize("hasAuthority('EXAM_PUBLISH') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> publishExam(@PathVariable Long examId) {
        examService.publishExam(securityUtil.getInstructorId(), examId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/exams/{examId}/close")
    @PreAuthorize("hasAuthority('EXAM_CLOSE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> closeExam(@PathVariable Long examId) {
        examService.closeExam(securityUtil.getInstructorId(), examId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/exams/{examId}")
    @PreAuthorize("hasAuthority('EXAM_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId) {
        examService.deleteExam(securityUtil.getInstructorId(), examId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evaluations/pending")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<PendingEvaluationProjection>> getPendingEvaluations() {
        return ResponseEntity.ok(examService.getPendingEvaluations(securityUtil.getInstructorId()));
    }

    @PostMapping("/evaluate/{responseId}")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> evaluateResponse(
            @PathVariable Long responseId,
            @RequestBody InstructorEvaluationRequestDTO request) {
        examService.evaluateResponse(securityUtil.getInstructorId(), responseId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exam-attempts/exam/{examId}")
    @PreAuthorize("hasAuthority('EXAM_ATTEMPT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<ExamAttempt>> getExamAttemptsByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamAttempts(securityUtil.getInstructorId(), examId));
    }

    // ==========================================
    // 5. WEBINARS
    // ==========================================
    @GetMapping("/webinars")
    @PreAuthorize("hasAuthority('WEBINAR_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Page<Webinar>> getInstructorWebinars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(webinarService.getInstructorWebinars(securityUtil.getInstructorId(), PageRequest.of(page, size)));
    }

    @GetMapping("/webinars/scheduled")
    @PreAuthorize("hasAuthority('WEBINAR_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<Webinar>> getScheduledWebinars() {
        return ResponseEntity.ok(webinarService.getScheduledWebinars(securityUtil.getInstructorId()));
    }

    @PostMapping("/webinars")
    @PreAuthorize("hasAuthority('WEBINAR_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Webinar> createWebinar(@RequestBody Webinar webinar) {
        return new ResponseEntity<>(webinarService.createWebinar(securityUtil.getInstructorId(), webinar), HttpStatus.CREATED);
    }

    @PutMapping("/webinars/{webinarId}")
    @PreAuthorize("hasAuthority('WEBINAR_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Webinar> updateWebinar(@PathVariable Long webinarId, @RequestBody Webinar webinar) {
        return ResponseEntity.ok(webinarService.updateWebinar(securityUtil.getInstructorId(), webinarId, webinar));
    }

    @PutMapping("/webinars/{webinarId}/cancel")
    @PreAuthorize("hasAuthority('WEBINAR_CANCEL') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> cancelWebinar(@PathVariable Long webinarId) {
        webinarService.cancelWebinar(securityUtil.getInstructorId(), webinarId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/webinars/{webinarId}/recording")
    @PreAuthorize("hasAuthority('WEBINAR_RECORDING_UPLOAD') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> uploadRecording(
            @PathVariable Long webinarId,
            @RequestParam("file") MultipartFile file) {

        webinarService.uploadRecording(securityUtil.getInstructorId(), webinarId, file);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // 6. CERTIFICATES
    // ==========================================
    @PostMapping("/certificates/{studentId}/generate")
    @PreAuthorize("hasAuthority('CERTIFICATE_GENERATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Certificate> generateCertificate(
            @PathVariable Long studentId,
            @RequestBody InstructorCertificateRequestDTO request) {
        return new ResponseEntity<>(
            certificateService.generateCertificateForStudent(securityUtil.getInstructorId(), studentId, request),
            HttpStatus.CREATED
        );
    }
}