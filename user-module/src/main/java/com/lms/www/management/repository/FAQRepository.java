package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.enums.TicketCategory;
import com.lms.www.management.model.FAQ;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByCategory(TicketCategory category);
}