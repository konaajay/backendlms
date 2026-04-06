package com.lms.www.management.dashboard.dto;

import java.time.LocalDate;

import com.lms.www.management.enums.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InstructorAttendanceRequestDTO {

    @NotNull
    private Long batchId;

    @NotNull
    private Long studentId;

    @NotNull
    private LocalDate attendanceDate;

    @NotNull
    private AttendanceStatus status;

    @Size(max = 255)
    private String remarks;

    private Long attendanceSessionId;
}