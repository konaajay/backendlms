package com.lms.www.affiliate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliatePayout;

public interface AffiliatePayoutRepository extends JpaRepository<AffiliatePayout, Long> {
    List<AffiliatePayout> findByAffiliate(Affiliate affiliate);

    List<AffiliatePayout> findByAffiliateId(Long affiliateId);

    boolean existsByAffiliateAndStatus(Affiliate affiliate, AffiliatePayout.PayoutStatus status);
    List<AffiliatePayout> findByAffiliateAndStatus(Affiliate affiliate, AffiliatePayout.PayoutStatus status);

    AffiliatePayout findTopByAffiliate_UserIdAndStatusOrderByCreatedAtDesc(Long userId, AffiliatePayout.PayoutStatus status);
}
