package com.lms.www.affiliate.service;

import java.math.BigDecimal;
import java.util.List;

import com.lms.www.affiliate.dto.AffiliateTransactionDTO;
import com.lms.www.affiliate.dto.AffiliateWalletDTO;
import com.lms.www.affiliate.dto.WalletConfigDTO;
import com.lms.www.affiliate.entity.AffiliateWallet;
import org.springframework.stereotype.Service;

@Service("affiliateWalletService")
public interface WalletService {
    
    // Admin / System Methods
    List<AffiliateWalletDTO> getAllWallets();
    AffiliateWalletDTO getWallet(Long affiliateId);
    AffiliateWallet getWalletEntity(Long affiliateId); // Internal use
    List<AffiliateTransactionDTO> getWalletTransactions(Long affiliateId);
    
    void creditCommission(Long affiliateId, BigDecimal amount, Long saleId, String description);
    void approvePayout(Long payoutId);
    void rejectPayout(Long payoutId, String reason);
    
    // Config
    WalletConfigDTO getWalletConfig();
    WalletConfigDTO updateWalletConfig(WalletConfigDTO configDTO);

    // Affiliate Secure Methods
    AffiliateWalletDTO getMyWalletSecure();
    List<AffiliateTransactionDTO> getMyTransactionsSecure();
    void requestPayoutSecure(BigDecimal amount);
    
    // Legacy support (optional, keep if needed by other services)
    void requestPayout(Long userId, BigDecimal amount);
    AffiliateWalletDTO getWalletByUserId(Long userId);
}
