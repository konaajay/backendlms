package com.lms.www.management.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.AttendanceAlertFlag;
import com.lms.www.management.model.AttendanceConfig;
import com.lms.www.management.model.AttendanceRecord;
import com.lms.www.management.model.AttendanceSession;
import com.lms.www.management.model.StudentAttendanceStatus;
import com.lms.www.management.repository.AttendanceAlertFlagRepository;
import com.lms.www.management.repository.AttendanceConfigRepository;
import com.lms.www.management.repository.AttendanceRecordRepository;
import com.lms.www.management.repository.AttendanceSessionRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.AttendanceRecordService;
import com.lms.www.management.service.EmailNotificationService;

@Service
@Transactional
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

        private final AttendanceRecordRepository attendanceRecordRepository;
        private final AttendanceSessionRepository attendanceSessionRepository;
        private final AttendanceConfigRepository attendanceConfigRepository;
        private final StudentBatchRepository studentBatchRepository;
        private final AttendanceAlertFlagRepository attendanceAlertFlagRepository;
        private final EmailNotificationService emailNotificationService;

        public AttendanceRecordServiceImpl(
                        AttendanceRecordRepository attendanceRecordRepository,
                        AttendanceSessionRepository attendanceSessionRepository,
                        AttendanceConfigRepository attendanceConfigRepository,
                        StudentBatchRepository studentBatchRepository,
                        AttendanceAlertFlagRepository attendanceAlertFlagRepository,
                        EmailNotificationService emailNotificationService) {
                this.attendanceRecordRepository = attendanceRecordRepository;
                this.attendanceSessionRepository = attendanceSessionRepository;
                this.attendanceConfigRepository = attendanceConfigRepository;
                this.studentBatchRepository = studentBatchRepository;
                this.attendanceAlertFlagRepository = attendanceAlertFlagRepository;
                this.emailNotificationService = emailNotificationService;
        }

        // ===============================
        // JOIN (MARK ATTENDANCE)
        // ===============================
        @Override
        public AttendanceRecord markAttendance(AttendanceRecord record) {
                return processMarking(record);
        }

        @Override
        @Transactional
        public List<AttendanceRecord> markAttendanceBulk(List<AttendanceRecord> records) {
                if (records == null || records.isEmpty()) {
                        return new ArrayList<>();
                }

                // 1. Fetch Session once for validation
                Long sessionId = records.get(0).getAttendanceSessionId();
                AttendanceSession session = attendanceSessionRepository.findById(sessionId)
                                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found: " + sessionId));
                
                validateSessionNotEnded(session);

                List<AttendanceRecord> results = new ArrayList<>();
                
                // 2. Process each record efficiently
                for (AttendanceRecord record : records) {
                        try {
                                // Ensure standard fields
                                applyDefaults(record, session);
                                if (record.getStatus() == null || record.getStatus().trim().isEmpty()) {
                                        record.setStatus("PRESENT");
                                }

                                // Check for existing
                                AttendanceRecord finalRecord = attendanceRecordRepository
                                                .findByAttendanceSessionIdAndStudentId(sessionId, record.getStudentId())
                                                .map(existing -> {
                                                        existing.setStatus(record.getStatus());
                                                        existing.setRemarks(record.getRemarks());
                                                        existing.setLateMinutes(record.getLateMinutes());
                                                        existing.setMarkedAt(LocalDateTime.now());
                                                        existing.setMarkedBy(record.getMarkedBy() != null ? record.getMarkedBy() : 1L);
                                                        return existing;
                                                })
                                                .orElse(record);

                                results.add(finalRecord);
                        } catch (Exception e) {
                                System.err.println("[Backend] Skip record for student " + record.getStudentId() + ": " + e.getMessage());
                        }
                }

                // 3. Batch save everything
                return attendanceRecordRepository.saveAll(results);
        }

        private AttendanceRecord processMarking(AttendanceRecord record) {
                AttendanceSession session = validateAttendanceInternal(record);
                validateSessionNotEnded(session);

                applyDefaults(record, session);

                if (record.getStatus() == null || record.getStatus().trim().isEmpty()) {
                        record.setStatus("PRESENT");
                }

                // Check for existing record by (attendanceSessionId, studentId)
                // This prevents session data from being overwritten if multiple sessions happen on the same day
                return attendanceRecordRepository.findByAttendanceSessionIdAndStudentId(
                                record.getAttendanceSessionId(),
                                record.getStudentId())
                                .map(existing -> {
                                        existing.setStatus(record.getStatus());
                                        existing.setRemarks(record.getRemarks());
                                        existing.setMarkedBy(record.getMarkedBy());
                                        existing.setMarkedAt(LocalDateTime.now());
                                        existing.setSource(record.getSource());
                                        return attendanceRecordRepository.save(existing);
                                })
                                .orElseGet(() -> attendanceRecordRepository.save(record));
        }

        // ===============================
        // LEAVE (FINALIZE ATTENDANCE)
        // ===============================
        @Override
        @Transactional
        public void markLeave(Long attendanceSessionId, Long studentId) {

                AttendanceRecord record = attendanceRecordRepository
                                .findByAttendanceSessionIdAndStudentId(
                                                attendanceSessionId, studentId)
                                .orElseThrow(() -> new IllegalStateException("Attendance not found"));

                AttendanceSession session = attendanceSessionRepository.findById(attendanceSessionId)
                                .orElseThrow(() -> new IllegalStateException("Session not found"));

                AttendanceConfig config = getConfig(session.getCourseId(), session.getBatchId());

                // Prevent duplicate leave
                if ("ABSENT".equals(record.getStatus())
                                || "PARTIAL".equals(record.getStatus())) {
                        return;
                }

                LocalDateTime leaveTime = LocalDateTime.now();
                record.setLeftAt(leaveTime);

                long sessionMinutes = Duration.between(
                                session.getStartedAt(),
                                leaveTime).toMinutes();

                long attendedMinutes = Duration.between(
                                record.getMarkedAt(),
                                leaveTime).toMinutes();

                if (sessionMinutes <= 0) {
                        throw new IllegalStateException("Invalid session duration");
                }

                int attendancePercent = (int) ((attendedMinutes * 100) / sessionMinutes);

                // ✅ APPLY STATUS
                if (attendedMinutes < config.getMinPresenceMinutes()) {

                        record.setStatus("ABSENT");
                        record.setRemarks("Left before minimum presence time");

                } else if (attendancePercent < config.getExamEligibilityPercent()) {

                        record.setStatus("PARTIAL");
                        record.setRemarks("Partial attendance");

                } else {

                        record.setStatus("PRESENT");
                        record.setRemarks("Attended and left");
                }

                attendanceRecordRepository.save(record);
        }

        // ===============================
        // VALIDATIONS
        // ===============================
        private AttendanceSession validateAttendanceInternal(AttendanceRecord record) {

                AttendanceSession session = attendanceSessionRepository
                                .findById(record.getAttendanceSessionId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Attendance session not found"));

                boolean enrolled = studentBatchRepository
                                .existsByStudentIdAndBatchIdAndStatus(
                                                record.getStudentId(),
                                                session.getBatchId(),
                                                "ACTIVE");

                if (!enrolled) {
                        throw new IllegalStateException(
                                        "Student is not enrolled in this batch");
                }

                return session;
        }


        private void validateSessionNotEnded(AttendanceSession session) {

                if ("ENDED".equalsIgnoreCase(session.getStatus())) {
                        throw new IllegalStateException(
                                        "Attendance session already ended");
                }

                if (session.getEndedAt() != null
                                && LocalDateTime.now().isAfter(session.getEndedAt())) {
                        throw new IllegalStateException(
                                        "Attendance time window closed");
                }
        }

        private void applyDefaults(
                        AttendanceRecord record,
                        AttendanceSession session) {

                if (record.getMarkedAt() == null) {
                        record.setMarkedAt(LocalDateTime.now());
                }

                if (record.getAttendanceDate() == null) {
                        record.setAttendanceDate(
                                        session.getStartedAt().toLocalDate());
                }

                if (record.getSource() == null) {
                        record.setSource("ONLINE");
                }

                if (record.getMarkedBy() == null) {
                        record.setMarkedBy(1L); // TEMP (JWT later)
                }

                if (record.getBatchId() == null) {
                        record.setBatchId(session.getBatchId());
                }
        }

        private AttendanceConfig getConfig(
                        Long courseId,
                        Long batchId) {
                return attendanceConfigRepository
                                .findByCourseIdAndBatchId(courseId, batchId)
                                .orElseGet(() -> {
                                        AttendanceConfig defaultCfg = new AttendanceConfig();
                                        defaultCfg.setExamEligibilityPercent(80);
                                        defaultCfg.setAtRiskPercent(75);
                                        defaultCfg.setLateGraceMinutes(15);
                                        defaultCfg.setMinPresenceMinutes(30);
                                        defaultCfg.setAutoAbsentMinutes(45);
                                        defaultCfg.setConsecutiveAbsenceLimit(3);
                                        defaultCfg.setEarlyExitAction("MARK_PARTIAL");
                                        return defaultCfg;
                                });
        }

        // ===============================
        // READ / UPDATE / DELETE
        // ===============================
        @Override
        public AttendanceRecord updateAttendance(
                        Long id,
                        AttendanceRecord incoming) {

                AttendanceRecord existing = attendanceRecordRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Attendance record not found"));

                if (incoming.getStatus() != null)
                        existing.setStatus(incoming.getStatus());

                if (incoming.getRemarks() != null)
                        existing.setRemarks(incoming.getRemarks());

                if (incoming.getLateMinutes() != null)
                        existing.setLateMinutes(incoming.getLateMinutes());

                return attendanceRecordRepository.save(existing);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceRecord> getByAttendanceSession(Long sessionId) {
                return attendanceRecordRepository
                                .findByAttendanceSessionId(sessionId);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceRecord> getByDate(LocalDate date) {
                return attendanceRecordRepository
                                .findByAttendanceDate(date);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceRecord> getBySessionAndDate(
                        Long sessionId,
                        LocalDate date) {
                return attendanceRecordRepository
                                .findByAttendanceSessionIdAndAttendanceDate(
                                                sessionId,
                                                date);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceRecord> getByStudent(Long studentId) {
                return attendanceRecordRepository
                                .findByStudentId(studentId);
        }

        @Override
        @Transactional(readOnly = true)
        public List<AttendanceRecord> getByBatch(Long batchId) {
                return attendanceRecordRepository.findByBatchId(batchId);
        }

        @Override
        public void delete(Long id) {

                AttendanceRecord record = attendanceRecordRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Attendance record not found"));

                attendanceRecordRepository.delete(record);
        }

        // ===============================
        // AT-RISK CHECK (READ ONLY)
        // ===============================
        @Transactional(readOnly = true)
        public boolean isStudentAtRiskByAbsence(
                        Long studentId,
                        Long courseId,
                        Long batchId) {

                AttendanceConfig config = attendanceConfigRepository
                                .findByCourseIdAndBatchId(courseId, batchId)
                                .orElseThrow(() -> new IllegalStateException("Attendance config not found"));

                int limit = config.getConsecutiveAbsenceLimit();

                // ✅ CORRECT: use Pageable
                List<AttendanceRecord> records = attendanceRecordRepository
                                .findByStudentIdOrderByAttendanceDateDesc(
                                                studentId,
                                                PageRequest.of(0, limit));

                int consecutiveAbsent = 0;

                for (AttendanceRecord record : records) {
                        if ("ABSENT".equals(record.getStatus())) {
                                consecutiveAbsent++;
                        } else {
                                break; // streak broken
                        }
                }

                return consecutiveAbsent >= limit;
        }

        @Transactional(readOnly = true)
        public int calculateAttendancePercentage(
                        Long studentId,
                        Long courseId,
                        Long batchId) {

                // 1. Get all sessions for course + batch
                List<AttendanceSession> sessions = attendanceSessionRepository
                                .findByCourseIdAndBatchId(courseId, batchId);

                if (sessions.isEmpty())
                        return 0;

                List<Long> sessionIds = sessions.stream()
                                .map(AttendanceSession::getId)
                                .toList();

                // 2. Total sessions
                long totalSessions = sessionIds.size();

                // 3. Attended sessions (PRESENT + PARTIAL)
                long attendedSessions = attendanceRecordRepository
                                .countByStudentIdAndAttendanceSessionIdInAndStatusIn(
                                                studentId,
                                                sessionIds,
                                                List.of("PRESENT", "PARTIAL"));

                // 4. Percentage
                return (int) ((attendedSessions * 100) / totalSessions);
        }

        @Transactional(readOnly = true)
        public int getAttendancePercentage(Long studentId, Long courseId, Long batchId) {

                // 1. Get all sessions
                List<AttendanceSession> sessions = attendanceSessionRepository.findByCourseIdAndBatchId(courseId,
                                batchId);

                if (sessions.isEmpty())
                        return 0;

                List<Long> sessionIds = sessions.stream()
                                .map(AttendanceSession::getId)
                                .toList();

                // 2. Total sessions
                long totalSessions = sessionIds.size();

                // 3. Attended sessions (PRESENT + PARTIAL)
                long attendedSessions = attendanceRecordRepository
                                .countByStudentIdAndAttendanceSessionIdInAndStatusIn(
                                                studentId,
                                                sessionIds,
                                                List.of("PRESENT", "PARTIAL"));

                // 4. Percentage
                return (int) ((attendedSessions * 100) / totalSessions);
        }

        @Override
        public boolean isStudentEligible(Long studentId, Long courseId, Long batchId) {
                AttendanceConfig config = attendanceConfigRepository
                                .findByCourseIdAndBatchId(courseId, batchId)
                                .orElseThrow(() -> new IllegalStateException("Attendance config not found"));

                int attendancePercent = getAttendancePercentage(studentId, courseId, batchId);

                return attendancePercent >= config.getExamEligibilityPercent();
        }

        @Override
        public boolean isStudentAtRisk(Long studentId, Long courseId, Long batchId) {
                AttendanceConfig config = attendanceConfigRepository
                                .findByCourseIdAndBatchId(courseId, batchId)
                                .orElseThrow(() -> new IllegalStateException("Attendance config not found"));

                int attendancePercent = getAttendancePercentage(studentId, courseId, batchId);

                return attendancePercent < config.getAtRiskPercent();
        }

        @Override
        @Transactional(readOnly = true)
        public List<StudentAttendanceStatus> getDashboardAttendanceStatus(
                        Long courseId,
                        Long batchId,
                        Long sessionId
        ) {
                if (batchId == null) {
                    System.out.println("[Attendance] Error: batchId is null");
                    return new ArrayList<>();
                }

                // 1. Get all student IDs from enrollment AND from existing attendance records
                Set<Long> studentIdSet = new HashSet<>();
                List<Long> enrolledIds = studentBatchRepository.findStudentIdsByBatchId(batchId);
                if (enrolledIds != null) studentIdSet.addAll(enrolledIds);
                
                // Add any students who have records in this batch but might not be in student_batch
                List<AttendanceRecord> batchRecords = attendanceRecordRepository.findByBatchId(batchId);
                if (batchRecords != null) {
                    for (AttendanceRecord ar : batchRecords) {
                        studentIdSet.add(ar.getStudentId());
                    }
                }
                
                List<Long> studentIds = new ArrayList<>(studentIdSet);
                
                System.out.println("[Attendance] Dashboard - Students found: " + studentIds.size() + " for batch: " + batchId);

                // 2. Prepare search context
                List<Long> sessionIds;
                long totalSessions;
                if (sessionId != null && sessionId > 0) {
                        sessionIds = List.of(sessionId);
                        totalSessions = 1;
                } else {
                        // Priority 1: Use course + batch
                        List<AttendanceSession> sessions = attendanceSessionRepository.findByCourseIdAndBatchId(courseId, batchId);
                        
                        // Priority 2: Use only batch if course-specific search yielded nothing
                        if (sessions == null || sessions.isEmpty()) {
                            // Find sessions just by batch ID using repository method to avoid findAll()
                            // Note: Assuming findByBatchId exists, if not we use finding by sessionId in statuses
                            sessions = attendanceSessionRepository.findAll().stream()
                                .filter(s -> batchId.equals(s.getBatchId()))
                                .toList();
                        }
                        totalSessions = sessions.size();
                        sessionIds = sessions.stream().map(AttendanceSession::getId).toList();
                }

                Map<Long, StudentAttendanceStatus> statusMap = new LinkedHashMap<>();
                AttendanceConfig config = getConfig(courseId, batchId);

                for (Long studentId : studentIds) {
                        long attendedSessions = 0;
                        if (!sessionIds.isEmpty()) {
                                attendedSessions = attendanceRecordRepository
                                                .countByStudentIdAndAttendanceSessionIdInAndStatusIn(
                                                                studentId,
                                                                sessionIds,
                                                                List.of("PRESENT", "PARTIAL"));
                        }

                        // If calculating for All Classes and student has 0 marks, and total sessions is 0, skip
                        if (sessionId == null && totalSessions == 0) continue;

                        int percent = totalSessions > 0 ? (int) ((attendedSessions * 100) / totalSessions) : 0;
                        boolean eligible = percent >= config.getExamEligibilityPercent();
                        boolean atRiskByPercent = percent < config.getAtRiskPercent();
                        boolean atRiskByAbsence = isStudentAtRiskByAbsence(studentId, courseId, batchId);

                        StudentAttendanceStatus status = new StudentAttendanceStatus();
                        status.setStudentId(studentId);
                        status.setAttendancePercent(percent);
                        status.setEligible(eligible);
                        status.setTotalSessions((int) totalSessions);
                        status.setAttendedSessions((int) attendedSessions);
                        status.setAbsentSessions((int) (totalSessions - attendedSessions));
                        status.setAtRiskByPercent(atRiskByPercent);
                        status.setAtRiskByAbsence(atRiskByAbsence);
                        status.setAlertRequired(atRiskByPercent || atRiskByAbsence);

                        if (atRiskByAbsence) {
                                status.setAlertReason("CONSECUTIVE_ABSENCE");
                        } else if (atRiskByPercent) {
                                status.setAlertReason("LOW_ATTENDANCE_PERCENT");
                        }

                        // Notifications
                        syncAndNotify(studentId, courseId, batchId, "AT_RISK_PERCENT", atRiskByPercent, percent);
                        syncAndNotify(studentId, courseId, batchId, "CONSECUTIVE_ABSENCE", atRiskByAbsence, percent);
                        syncAndNotify(studentId, courseId, batchId, "NOT_ELIGIBLE", !eligible, percent);

                        statusMap.put(studentId, status);
                }

                return new ArrayList<>(statusMap.values());
        }

        @Transactional(readOnly = true)
        public String getStudentAttendanceFlag(
                        Long studentId,
                        Long courseId,
                        Long batchId) {

                // 1️⃣ Check consecutive absence (HIGH PRIORITY)
                boolean atRiskByAbsence = isStudentAtRiskByAbsence(studentId, courseId, batchId);

                if (atRiskByAbsence) {
                        return "AT_RISK_ABSENCE";
                }

                // 2️⃣ Calculate attendance percentage
                int attendancePercent = getAttendancePercentage(studentId, courseId, batchId);

                AttendanceConfig config = attendanceConfigRepository
                                .findByCourseIdAndBatchId(courseId, batchId)
                                .orElseThrow(() -> new IllegalStateException("Attendance config not found"));

                // 3️⃣ Eligibility check
                if (attendancePercent < config.getExamEligibilityPercent()) {
                        return "NOT_ELIGIBLE";
                }

                // 4️⃣ At-risk percentage check
                if (attendancePercent < config.getAtRiskPercent()) {
                        return "AT_RISK_PERCENTAGE";
                }

                // 5️⃣ Safe
                return "OK";
        }

        private void syncAlertFlag(
                        Long studentId,
                        Long courseId,
                        Long batchId,
                        String flagType,
                        boolean conditionActive) {

                attendanceAlertFlagRepository
                                .findByStudentIdAndCourseIdAndBatchIdAndFlagType(
                                                studentId, courseId, batchId, flagType)
                                .ifPresentOrElse(flag -> {

                                        // CONDITION RESOLVED → close flag
                                        if (!conditionActive && "ACTIVE".equals(flag.getStatus())) {
                                                flag.setStatus("RESOLVED");
                                                flag.setResolvedAt(LocalDateTime.now());
                                                attendanceAlertFlagRepository.save(flag);
                                        }

                                }, () -> {

                                        // NEW RISK → create flag + SEND EMAIL ONCE
                                        if (conditionActive) {

                                                AttendanceAlertFlag flag = new AttendanceAlertFlag();
                                                flag.setStudentId(studentId);
                                                flag.setCourseId(courseId);
                                                flag.setBatchId(batchId);
                                                flag.setFlagType(flagType);

                                                attendanceAlertFlagRepository.save(flag);

                                                // 🔔 EXACT LINE YOU ADD
                                                emailNotificationService.sendAttendanceAlert(
                                                                studentId,
                                                                flagType,
                                                                getAttendancePercentage(studentId, courseId, batchId));
                                        }
                                });
        }

        private void syncAndNotify(
                        Long studentId,
                        Long courseId,
                        Long batchId,
                        String flagType,
                        boolean conditionActive,
                        int attendancePercent) {

                attendanceAlertFlagRepository
                                .findByStudentIdAndCourseIdAndBatchIdAndFlagType(
                                                studentId, courseId, batchId, flagType)
                                .ifPresentOrElse(flag -> {

                                        // condition cleared → resolve flag
                                        if (!conditionActive && "ACTIVE".equals(flag.getStatus())) {
                                                flag.setStatus("RESOLVED");
                                                flag.setResolvedAt(LocalDateTime.now());
                                                attendanceAlertFlagRepository.save(flag);
                                        }

                                }, () -> {

                                        // NEW risk → create flag + SEND AUTO EMAIL
                                        if (conditionActive) {
                                                AttendanceAlertFlag flag = new AttendanceAlertFlag();
                                                flag.setStudentId(studentId);
                                                flag.setCourseId(courseId);
                                                flag.setBatchId(batchId);
                                                flag.setFlagType(flagType);
                                                attendanceAlertFlagRepository.save(flag);

                                                // 🔔 AUTO EMAIL (ONCE)
                                                emailNotificationService.sendAttendanceAlert(
                                                                studentId,
                                                                flagType,
                                                                attendancePercent);
                                        }
                                });
        }

}
