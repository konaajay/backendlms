package com.lms.www.affiliate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateLead;

public interface AffiliateLeadRepository extends JpaRepository<AffiliateLead, Long> {
    Optional<AffiliateLead> findByMobileAndBatchId(String mobile, Long batchId);
    
    List<AffiliateLead> findByAffiliate(Affiliate affiliate);
    List<AffiliateLead> findByAffiliateId(Long affiliateId);
    
    List<AffiliateLead> findByAffiliateOrderByCreatedAtDesc(Affiliate affiliate);
    List<AffiliateLead> findByAffiliateIdOrderByCreatedAtDesc(Long affiliateId);
    
    Long countByAffiliate(Affiliate affiliate);
    Long countByAffiliateId(Long affiliateId);
    
    Long countByAffiliateAndStatus(Affiliate affiliate, AffiliateLead.LeadStatus status);
    Long countByAffiliateIdAndStatus(Long affiliateId, AffiliateLead.LeadStatus status);
    
    Long countByLinkId(Long linkId);
    Long countByLinkIdAndStatus(Long linkId, AffiliateLead.LeadStatus status);
    
    Optional<AffiliateLead> findByEmailAndBatchId(String email, Long batchId);
    Optional<AffiliateLead> findByStudentId(Long studentId);
    
    List<AffiliateLead> findAllByOrderByCreatedAtDesc();
}
