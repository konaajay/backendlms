package com.lms.www.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lms.www.management.enums.TicketCategory;
import com.lms.www.management.enums.TicketStatus;
import com.lms.www.management.model.SupportTicket;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    @Query("SELECT t FROM SupportTicket t WHERE t.studentId = :studentId " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:category IS NULL OR t.category = :category)")
    Page<SupportTicket> findFilteredTicketsByStudent(
            @Param("studentId") Long studentId,
            @Param("status") TicketStatus status,
            @Param("category") TicketCategory category,
            Pageable pageable);

    long count();
}