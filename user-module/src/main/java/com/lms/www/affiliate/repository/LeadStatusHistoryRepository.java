package com.lms.www.affiliate.repository;

import com.lms.www.affiliate.entity.LeadStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeadStatusHistoryRepository extends JpaRepository<LeadStatusHistory, Long> {
    List<LeadStatusHistory> findByLeadIdOrderByTimestampDesc(Long leadId);
}
