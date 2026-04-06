package com.lms.www.affiliate.repository;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AffiliateWalletTransactionRepository extends JpaRepository<AffiliateWalletTransaction, Long> {
    List<AffiliateWalletTransaction> findByAffiliateOrderByCreatedAtDesc(Affiliate affiliate);

    List<AffiliateWalletTransaction> findByAffiliateIdOrderByCreatedAtDesc(Long affiliateId);

    boolean existsBySaleId(Long saleId);

}
