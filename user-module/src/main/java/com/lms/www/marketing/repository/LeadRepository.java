package com.lms.www.marketing.repository;

import com.lms.www.marketing.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("marketingModuleLeadRepository")
public interface LeadRepository extends JpaRepository<Lead, Long> {
    boolean existsByEmailAndBatchId(String email, Long batchId);
    boolean existsByEmailAndUtmCampaign(String email, String utmCampaign);
    long countByUtmSourceAndUtmCampaign(String utmSource, String utmCampaign);

}
