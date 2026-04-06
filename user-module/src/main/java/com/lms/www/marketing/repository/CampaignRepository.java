package com.lms.www.marketing.repository;

import com.lms.www.marketing.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    java.util.Optional<Campaign> findByCampaignName(String campaignName);
}
