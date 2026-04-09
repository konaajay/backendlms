package com.lms.www.management.dashboard.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.lms.www.management.repository.BatchRepository;

import org.springframework.stereotype.Service;

import com.lms.www.management.dashboard.dto.AttendanceSummaryDTO;
import com.lms.www.management.dashboard.dto.BatchSummaryDTO;
import com.lms.www.management.dashboard.dto.CertificateSummaryDTO;
import com.lms.www.management.dashboard.dto.ExamSummaryDTO;
import com.lms.www.management.dashboard.dto.SessionContentDTO;
import com.lms.www.management.dashboard.dto.SessionProgressDTO;
import com.lms.www.management.dashboard.dto.WebinarSummaryDTO;
import com.lms.www.management.dashboard.service.DashboardMetricsService;
import com.lms.www.management.model.AttendanceRecord;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamSchedule;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.SessionContent;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.model.WebinarRegistration;
import com.lms.www.management.repository.AttendanceRecordRepository;
import com.lms.www.management.repository.CertificateRepository;
import com.lms.www.management.repository.ExamAttemptRepository;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.ExamScheduleRepository;
import com.lms.www.management.repository.SessionContentRepository;
import com.lms.www.management.repository.SessionRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.repository.WebinarRegistrationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardMetricsServiceImpl implements DashboardMetricsService {

    private final StudentBatchRepository studentBatchRepository;
    private final AttendanceRecordRepository attendanceRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CertificateRepository certificateRepository;
    private final WebinarRegistrationRepository webinarRegistrationRepository;
    private final SessionContentRepository sessionContentRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final ExamRepository examRepository;
    

    private final SessionRepository sessionRepository; // ✅ ADDED
    private final BatchRepository batchRepository;

    @Override
    public List<BatchSummaryDTO> getBatchesForStudent(Long studentId) {
        // 🔥 Get all user batches
        List<StudentBatch> batchesByStudentId = studentBatchRepository.findByStudentIdWithBatch(studentId);
        List<StudentBatch> batchesByUserId = studentBatchRepository.findByUserId(studentId);
        
        java.util.Set<Long> processedBatchIds = new java.util.HashSet<>();
        List<StudentBatch> userBatches = new java.util.ArrayList<>();
        
        for (StudentBatch sb : batchesByStudentId) {
            if (processedBatchIds.add(sb.getStudentBatchId())) {
                userBatches.add(sb);
            }
        }
        for (StudentBatch sb : batchesByUserId) {
            if (processedBatchIds.add(sb.getStudentBatchId())) {
                userBatches.add(sb);
            }
        }

        if (userBatches.isEmpty()) return java.util.Collections.emptyList();

        List<Long> batchIds = userBatches.stream().map(StudentBatch::getBatchId).distinct().toList();

        // 1. Batch fetch all sessions for all batches
        List<Session> allSessions = sessionRepository.findByBatchIdIn(batchIds);
        java.util.Map<Long, List<Session>> sessionsByBatch = allSessions.stream()
                .collect(Collectors.groupingBy(Session::getBatchId));

        // 2. Batch fetch all session contents for all sessions
        List<Long> sessionIds = allSessions.stream().map(Session::getSessionId).toList();
        java.util.Map<Long, List<SessionContent>> contentsBySession = java.util.Collections.emptyMap();
        if (!sessionIds.isEmpty()) {
            contentsBySession = sessionContentRepository.findBySessionIdIn(sessionIds).stream()
                    .collect(Collectors.groupingBy(SessionContent::getSessionId));
        }

        final java.util.Map<Long, List<SessionContent>> finalContentsBySession = contentsBySession;

        return userBatches.stream()
                .map(sb -> {
                    // Refresh batch if needed
                    if (sb.getBatch() == null) {
                        try {
                            sb.setBatch(batchRepository.findById(sb.getBatchId()).orElse(null));
                        } catch (Exception e) {}
                    }

                    List<Session> sessions = sessionsByBatch.getOrDefault(sb.getBatchId(), java.util.Collections.emptyList());

                    List<SessionProgressDTO> sessionDTOs = sessions.stream()
                            .map(session -> {
                                List<SessionContent> contents = finalContentsBySession.getOrDefault(session.getSessionId(), java.util.Collections.emptyList());
                                
                                List<SessionContentDTO> contentDTOs = contents.stream()
                                        .map(c -> SessionContentDTO.builder()
                                                .sessionContentId(c.getSessionContentId())
                                                .title(c.getTitle())
                                                .description(c.getDescription())
                                                .contentType(c.getContentType())
                                                .fileUrl(c.getFileUrl())
                                                .status(c.getStatus())
                                                .totalDuration(c.getTotalDuration())
                                                .build())
                                        .collect(Collectors.toList());

                                return SessionProgressDTO.builder()
                                        .sessionId(session.getSessionId())
                                        .sessionName(session.getSessionName())
                                        .type(session.getSessionType())
                                        .completed(false)
                                        .videoProgressPercentage(0.0)
                                        .contents(contentDTOs)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return BatchSummaryDTO.builder()
                            .batchId(sb.getBatchId())
                            .batchName(sb.getBatch() != null ? sb.getBatch().getBatchName() : "Unnamed Batch")
                            .startDate(sb.getBatch() != null ? sb.getBatch().getStartDate() : null)
                            .endDate(sb.getBatch() != null ? sb.getBatch().getEndDate() : null)
                            .instructorInfo(sb.getBatch() != null && sb.getBatch().getTrainerName() != null
                                            ? sb.getBatch().getTrainerName()
                                            : "TBD")
                            .sessions(sessionDTOs)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceSummaryDTO getAttendanceForStudent(Long studentId) {

        List<AttendanceRecord> records = attendanceRepository.findByStudentId(studentId);

        int total = records.size();
        int attended = 0;
        int missed = 0;

        for (AttendanceRecord record : records) {

            if ("PRESENT".equalsIgnoreCase(record.getStatus())
                    || "LATE".equalsIgnoreCase(record.getStatus())) {
                attended++;
            } else if ("ABSENT".equalsIgnoreCase(record.getStatus())) {
                missed++;
            }
        }

        double percentage = total > 0 ? ((double) attended / total) * 100 : 0.0;

        return AttendanceSummaryDTO.builder()
                .totalSessions(total)
                .attendedSessions(attended)
                .missedSessions(missed)
                .attendancePercentage(percentage)
                .build();
    }

    @Override
    public List<ExamSummaryDTO> getExamsForStudent(Long studentId) {
        List<StudentBatch> batches = studentBatchRepository.findByStudentId(studentId);
        List<Long> batchIds = batches.stream().map(StudentBatch::getBatchId).toList();

        if (batchIds.isEmpty()) return java.util.Collections.emptyList();

        LocalDateTime now = LocalDateTime.now();
        List<ExamSchedule> activeSchedules = examScheduleRepository.findByBatchIdInAndIsActiveTrue(batchIds).stream()
                .filter(s -> now.isAfter(s.getStartTime()) && now.isBefore(s.getEndTime()))
                .toList();

        if (activeSchedules.isEmpty()) return java.util.Collections.emptyList();

        List<Long> examIds = activeSchedules.stream().map(ExamSchedule::getExamId).distinct().toList();

        // Batch fetch Exams
        java.util.Map<Long, Exam> examMap = examRepository.findAllById(examIds).stream()
                .collect(Collectors.toMap(Exam::getExamId, e -> e));

        // Batch fetch all attempts for these exams by this student
        List<ExamAttempt> allAttempts = examAttemptRepository.findByStudentIdAndExamIdIn(studentId, examIds);
        
        // Find latest attempt per exam
        java.util.Map<Long, ExamAttempt> latestAttemptMap = allAttempts.stream()
                .collect(Collectors.toMap(
                    ExamAttempt::getExamId,
                    a -> a,
                    (existing, replacement) -> existing.getStartTime().isAfter(replacement.getStartTime()) ? existing : replacement
                ));

        return activeSchedules.stream()
                .map(s -> {
                    Exam exam = examMap.get(s.getExamId());
                    if (exam == null) return null;

                    ExamAttempt latestAttempt = latestAttemptMap.get(exam.getExamId());

                    if (latestAttempt != null) {
                        return ExamSummaryDTO.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getTitle())
                                .attemptStatus(latestAttempt.getStatus())
                                .score(latestAttempt.getScore() != null ? latestAttempt.getScore() : 0.0)
                                .passFailStatus("N/A")
                                .attemptDate(latestAttempt.getStartTime())
                                .build();
                    } else {
                        return ExamSummaryDTO.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getTitle())
                                .attemptStatus("NOT_ATTEMPTED")
                                .score(0.0)
                                .passFailStatus("N/A")
                                .attemptDate(null)
                                .build();
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
    
    
    @Override
    public List<CertificateSummaryDTO> getCertificatesForStudent(Long studentId) {

        List<Certificate> certs = certificateRepository.findByUserId(studentId);

        return certs.stream()
                .map(cert -> CertificateSummaryDTO.builder()
                        .certificateId(cert.getId())
                        .certificateName(cert.getEventTitle())
                        .issueDate(cert.getIssuedDate())
                        .expiryDate(cert.getExpiryDate())
                        .certificateStatus(cert.getStatus() != null ? cert.getStatus().name() : "ISSUED")
                        .downloadUrl(cert.getPdfUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<WebinarSummaryDTO> getWebinarsForStudent(Long studentId) {

        List<WebinarRegistration> registrations =
                webinarRegistrationRepository.findByUserId(studentId);

        return registrations.stream()
                .map(reg -> {
                    if (reg.getWebinar() == null) return null;
                    return WebinarSummaryDTO.builder()
                        .webinarId(reg.getWebinar().getWebinarId())
                        .webinarTitle(reg.getWebinar().getTitle())
                        .registrationStatus(
                                reg.getRegistrationStatus() != null
                                        ? reg.getRegistrationStatus().name()
                                        : "CONFIRMED")
                        .attendanceStatus("N/A")
                        .webinarDate(reg.getWebinar().getStartTime())
                        .recordingUrl(null)
                        .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}