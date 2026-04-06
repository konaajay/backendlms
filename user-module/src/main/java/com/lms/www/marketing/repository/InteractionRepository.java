package com.lms.www.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.marketing.model.Interaction;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByCampaignCampaignId(Long campaignId);

    List<Interaction> findByCustomerEmail(String customerEmail);
}
