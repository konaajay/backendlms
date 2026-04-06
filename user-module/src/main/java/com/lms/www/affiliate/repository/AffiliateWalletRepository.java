package com.lms.www.affiliate.repository;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface AffiliateWalletRepository extends JpaRepository<AffiliateWallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AffiliateWallet> findByAffiliate(Affiliate affiliate);

    Optional<AffiliateWallet> findByAffiliateId(Long affiliateId);
}
