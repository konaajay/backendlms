package com.lms.www.marketing.repository;

import com.lms.www.marketing.model.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {
    Optional<ReferralCode> findByCode(String code);

    Optional<ReferralCode> findByCodeIgnoreCase(String code);

    boolean existsByCode(String code);

    boolean existsByCodeIgnoreCase(String code);

    Optional<ReferralCode> findByUserId(Long userId);
}
