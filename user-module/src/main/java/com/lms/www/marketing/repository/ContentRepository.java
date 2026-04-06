package com.lms.www.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.marketing.model.Content;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCampaignCampaignId(Long campaignId);
}
