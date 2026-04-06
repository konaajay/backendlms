package com.lms.www.marketing.repository;

import com.lms.www.affiliate.entity.AffiliateClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<AffiliateClick, Long> {
    List<AffiliateClick> findByAffiliateCode(String affiliateCode);
}
