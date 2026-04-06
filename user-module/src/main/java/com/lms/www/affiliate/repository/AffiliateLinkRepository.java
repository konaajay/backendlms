package com.lms.www.affiliate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateLink;

public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {
    Optional<AffiliateLink> findByAffiliateAndBatchId(Affiliate affiliate, Long batchId);
    Optional<AffiliateLink> findByAffiliateIdAndBatchId(Long affiliateId, Long batchId);
    
    Optional<AffiliateLink> findByReferralCode(String referralCode);
    
    List<AffiliateLink> findByAffiliate(Affiliate affiliate);
    List<AffiliateLink> findByAffiliateId(Long affiliateId);
}
