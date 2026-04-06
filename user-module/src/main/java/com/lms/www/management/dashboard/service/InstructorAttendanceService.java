package com.lms.www.management.dashboard.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.www.management.instructor.dto.InstructorAttendanceRequestDTO;
import com.lms.www.management.model.AttendanceRecord;

public interface InstructorAttendanceService {

        List<AttendanceRecord> getAttendanceByBatchAndDate(
                        Long instructorId,
                        Long batchId,
                        LocalDate date);

        AttendanceRecord markStudentAttendance(
                        Long instructorId,
                        InstructorAttendanceRequestDTO request);
}