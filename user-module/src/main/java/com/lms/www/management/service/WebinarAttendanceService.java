package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.enums.AttendanceMode;
import com.lms.www.management.enums.AttendanceStatus;
import com.lms.www.management.model.WebinarAttendance;

public interface WebinarAttendanceService {

    WebinarAttendance markAttendance(Long registrationId, AttendanceStatus status, AttendanceMode mode);

    List<WebinarAttendance> getAttendanceByWebinar(Long webinarId);

    List<WebinarAttendance> getAttendanceByRegistration(Long registrationId);
}