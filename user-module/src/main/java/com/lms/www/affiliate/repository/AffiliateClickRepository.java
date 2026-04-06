package com.lms.www.affiliate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.affiliate.entity.AffiliateClick;

public interface AffiliateClickRepository extends JpaRepository<AffiliateClick, Long> {
    List<AffiliateClick> findByAffiliateCode(String affiliateCode);

    long countByAffiliateCode(String affiliateCode);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(c) FROM AffiliateClick c WHERE c.affiliateCode IN :codes")
    long countByAffiliateCodeIn(java.util.List<String> codes);
    
    Long countByBatchIdAndAffiliateCode(Long batchId, String affiliateCode);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(c) > 0 FROM AffiliateClick c
        WHERE c.affiliateCode = :code
        AND c.ipAddress = :ip
        AND c.clickedAt >= :time
    """)
    boolean existsRecent(String code, String ip, java.time.LocalDateTime time);

    void deleteAllByClickedAtBefore(java.time.LocalDateTime time);
}
