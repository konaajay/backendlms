package com.lms.www.management.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.model.StudentCalendarEvent;

@Repository
public interface StudentCalendarEventRepository extends JpaRepository<StudentCalendarEvent, Long> {

    @Query("SELECT e FROM StudentCalendarEvent e WHERE e.userId = :userId AND e.eventDate BETWEEN :startDate AND :endDate")
    List<StudentCalendarEvent> findByUserIdAndEventDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<StudentBatch> findByUserId(Long userId);
}