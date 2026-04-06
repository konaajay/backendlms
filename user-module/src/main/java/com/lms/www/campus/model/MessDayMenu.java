package com.lms.www.campus.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

// REDUNDANT - MOVED TO com.lms.www.campus.Hostel
@Data
public class MessDayMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY,
        THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // -------- Meals --------
    private String breakfast;
    private String lunch;
    private String dinner;

}