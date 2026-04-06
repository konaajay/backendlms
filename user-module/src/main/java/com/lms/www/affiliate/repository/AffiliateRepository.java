package com.lms.www.affiliate.repository;

import com.lms.www.affiliate.entity.Affiliate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {
    // removed findByCode

    Optional<Affiliate> findByReferralCode(String referralCode);

    Optional<Affiliate> findByUsername(String username);

    Optional<Affiliate> findByEmail(String email);

    Optional<Affiliate> findByUserId(Long userId);

    java.util.List<Affiliate> findByType(com.lms.www.affiliate.entity.AffiliateType type);

    java.util.List<Affiliate> findByTypeNot(com.lms.www.affiliate.entity.AffiliateType type);

    boolean existsByUsername(String username);

    // removed existsByCode
}
