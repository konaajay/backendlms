package com.lms.www.management.dashboard.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<BatchSummaryDTO> getBatchesForStudent(Long studentId) {

        List<StudentBatch> userBatches = studentBatchRepository.findByStudentIdWithBatch(studentId);

        return userBatches.stream()
                .map(sb -> {

                    // ✅ FETCH SESSIONS FOR THIS BATCH
                    List<Session> sessions = sessionRepository.findByBatchId(
                            sb.getBatch().getBatchId()
                    );

                    // ✅ MAP TO DTO
                    List<SessionProgressDTO> sessionDTOs = sessions.stream()
                            .map(session -> {

                                // 🔥 GET CONTENTS
                                List<SessionContentDTO> contentDTOs =
                                        sessionContentRepository.findBySessionId(session.getSessionId())
                                                .stream()
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
                                        .contents(contentDTOs) // ✅ ADD THIS
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return BatchSummaryDTO.builder()
                            .batchId(sb.getBatch().getBatchId())
                            .batchName(sb.getBatch().getBatchName())
                            .startDate(sb.getBatch().getStartDate())
                            .endDate(sb.getBatch().getEndDate())
                            .instructorInfo(
                                    sb.getBatch().getTrainerName() != null
                                            ? sb.getBatch().getTrainerName()
                                            : "TBD")
                            .sessions(sessionDTOs) // ✅ IMPORTANT
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

        List<Long> batchIds = batches.stream()
                .map(sb -> sb.getBatch().getBatchId())
                .toList();

        LocalDateTime now = LocalDateTime.now();

        List<ExamSchedule> schedules =
                examScheduleRepository.findByBatchIdInAndIsActiveTrue(batchIds);

        return schedules.stream()
                .filter(s -> now.isAfter(s.getStartTime()) && now.isBefore(s.getEndTime()))
                .map(s -> {

                    Exam exam = examRepository.findById(s.getExamId()).orElse(null);
                    if (exam == null) return null;

                    Optional<ExamAttempt> attemptOpt =
                            examAttemptRepository.findFirstByStudentIdAndExamIdOrderByStartTimeDesc(studentId, exam.getExamId());

                    if (attemptOpt.isPresent()) {
                        ExamAttempt attempt = attemptOpt.get();

                        return ExamSummaryDTO.builder()
                                .examId(exam.getExamId())
                                .examName(exam.getTitle())
                                .attemptStatus(attempt.getStatus())
                                .score(attempt.getScore() != null ? attempt.getScore() : 0.0)
                                .passFailStatus("N/A")
                                .attemptDate(attempt.getStartTime())
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
                .filter(e -> e != null)
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
                .map(reg -> WebinarSummaryDTO.builder()
                        .webinarId(reg.getWebinar().getWebinarId())
                        .webinarTitle(reg.getWebinar().getTitle())
                        .registrationStatus(
                                reg.getRegistrationStatus() != null
                                        ? reg.getRegistrationStatus().name()
                                        : "CONFIRMED")
                        .attendanceStatus("N/A")
                        .webinarDate(reg.getWebinar().getStartTime())
                        .recordingUrl(null)
                        .build())
                .collect(Collectors.toList());
    }
}