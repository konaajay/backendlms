package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.dashboard.dto.CalendarEventDTO;
import com.lms.www.management.dashboard.dto.PersonalEventRequestDTO;

public interface StudentCalendarService {

    List<CalendarEventDTO> getStudentCalendarEvents(Long userId, int year, int month);

    void addPersonalEvent(Long userId, PersonalEventRequestDTO request);

}