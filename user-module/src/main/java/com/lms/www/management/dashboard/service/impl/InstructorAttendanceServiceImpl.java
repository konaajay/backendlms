package com.lms.www.management.dashboard.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.dashboard.service.InstructorAttendanceService;
import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.instructor.dto.InstructorAttendanceRequestDTO;
import com.lms.www.management.model.AttendanceRecord;
import com.lms.www.management.model.Batch;
import com.lms.www.management.repository.AttendanceRecordRepository;
import com.lms.www.management.repository.BatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorAttendanceServiceImpl implements InstructorAttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final BatchRepository batchRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceByBatchAndDate(Long instructorId, Long batchId, LocalDate date) {
        validateBatchOwnership(instructorId, batchId);
        return attendanceRecordRepository.findByBatchIdAndAttendanceDate(batchId, date);
    }

    @Override
    @Transactional
    public AttendanceRecord markStudentAttendance(
            Long instructorId,
            InstructorAttendanceRequestDTO request) {

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Attendance status required");
        }

        if (request.getAttendanceSessionId() == null) {
            throw new IllegalArgumentException("AttendanceSessionId is required");
        }

        validateBatchOwnership(instructorId, request.getBatchId());

        Optional<AttendanceRecord> existing = attendanceRecordRepository.findByBatchIdAndStudentIdAndAttendanceDate(
                request.getBatchId(),
                request.getStudentId(),
                request.getAttendanceDate());

        AttendanceRecord record;

        if (existing.isPresent()) {
            record = existing.get();
            record.setStatus(request.getStatus().name());
            record.setRemarks(request.getRemarks());
        } else {
            record = new AttendanceRecord();
            record.setBatchId(request.getBatchId());
            record.setStudentId(request.getStudentId());
            record.setAttendanceDate(request.getAttendanceDate());
            record.setStatus(request.getStatus().name());
            record.setRemarks(request.getRemarks());
            record.setSource("INSTRUCTOR_PORTAL");
            record.setMarkedBy(instructorId);
            record.setAttendanceSessionId(request.getAttendanceSessionId());
        }

        return attendanceRecordRepository.save(record);
    }

    private void validateBatchOwnership(Long instructorId, Long batchId) {
        Batch batch = batchRepository.findByBatchIdAndTrainerId(batchId, instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found or unauthorized"));

        if (!instructorId.equals(batch.getTrainerId())) {
            throw new AccessDeniedException("Unauthorized to access this batch");
        }
    }
}