package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.dashboard.dto.CalendarEventDTO;
import com.lms.www.management.dashboard.dto.PersonalEventRequestDTO;
import com.lms.www.management.service.StudentCalendarService;
import com.lms.www.management.util.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/calendar")
@RequiredArgsConstructor
public class StudentCalendarController {

    private final StudentCalendarService studentCalendarService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<List<CalendarEventDTO>> getMonthEvents(
            @RequestParam int year,
            @RequestParam int month) {

        Long userId = securityUtil.getUserId();
        List<CalendarEventDTO> events = studentCalendarService.getStudentCalendarEvents(userId, year, month);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/personal")
    public ResponseEntity<String> addPersonalEvent(@Valid @RequestBody PersonalEventRequestDTO request) {
        Long userId = securityUtil.getUserId();
        studentCalendarService.addPersonalEvent(userId, request);
        return ResponseEntity.ok("Personal event added successfully");
    }
}