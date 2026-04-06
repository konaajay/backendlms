package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.AttendanceMode;
import com.lms.www.management.enums.AttendanceStatus;
import com.lms.www.management.enums.RegistrationStatus;
import com.lms.www.management.model.WebinarAttendance;
import com.lms.www.management.model.WebinarRegistration;
import com.lms.www.management.repository.WebinarAttendanceRepository;
import com.lms.www.management.repository.WebinarRegistrationRepository;
import com.lms.www.management.service.WebinarAttendanceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebinarAttendanceServiceImpl implements WebinarAttendanceService {

    private final WebinarAttendanceRepository attendanceRepository;
    private final WebinarRegistrationRepository registrationRepository;

    @Override
    @Transactional
    public WebinarAttendance markAttendance(Long registrationId, AttendanceStatus status, AttendanceMode mode) {

        WebinarRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getRegistrationStatus() != RegistrationStatus.REGISTERED) {
            throw new RuntimeException("Cannot mark attendance. Registration status is not REGISTERED.");
        }

        List<WebinarAttendance> records = attendanceRepository.findByRegistration_RegistrationId(registrationId);

        WebinarAttendance attendance;

        if (records.isEmpty()) {
            attendance = new WebinarAttendance();
            attendance.setRegistration(registration);
            attendance.setWebinar(registration.getWebinar());
            attendance.setJoinTime(LocalDateTime.now());
        } else {
            attendance = records.get(0);
        }

        attendance.setAttendanceStatus(status);
        attendance.setAttendanceMode(mode);

        return attendanceRepository.save(attendance);
    }

    @Override
    public List<WebinarAttendance> getAttendanceByWebinar(Long webinarId) {
        return attendanceRepository.findByWebinar_WebinarId(webinarId);
    }

    @Override
    public List<WebinarAttendance> getAttendanceByRegistration(Long registrationId) {
        return attendanceRepository.findByRegistration_RegistrationId(registrationId);
    }
}