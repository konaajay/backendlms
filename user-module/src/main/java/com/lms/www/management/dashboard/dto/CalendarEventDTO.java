package com.lms.www.management.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.lms.www.management.enums.CalendarEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {

    private String id;
    private String title;
    private String description;
    private CalendarEventType type;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}