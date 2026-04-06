package com.lms.www.affiliate.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.affiliate.dto.AffiliateTransactionDTO;
import com.lms.www.affiliate.dto.AffiliateWalletDTO;
import com.lms.www.affiliate.dto.WalletConfigDTO;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliatePayout;
import com.lms.www.affiliate.entity.AffiliateType;
import com.lms.www.affiliate.entity.AffiliateWallet;
import com.lms.www.affiliate.entity.AffiliateWalletTransaction;
import com.lms.www.affiliate.entity.WalletConfig;
import com.lms.www.affiliate.repository.AffiliatePayoutRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.repository.AffiliateWalletRepository;
import com.lms.www.affiliate.repository.AffiliateWalletTransactionRepository;
import com.lms.www.affiliate.repository.WalletConfigRepository;
import com.lms.www.affiliate.service.WalletService;
import com.lms.www.security.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("affiliateWalletService")
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateWalletRepository walletRepository;
    private final AffiliateWalletTransactionRepository transactionRepository;
    private final AffiliatePayoutRepository payoutRepository;
    private final WalletConfigRepository walletConfigRepository;
    private final UserContext userContext;

    // ================= ADMIN / SYSTEM METHODS =================

    @Override
    @Transactional(readOnly = true)
    public List<AffiliateWalletDTO> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(this::mapToWalletDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliateTransactionDTO> getWalletTransactions(Long affiliateId) {
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));
        return transactionRepository.findByAffiliateOrderByCreatedAtDesc(affiliate).stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AffiliateWalletDTO getWallet(Long affiliateId) {
        return mapToWalletDTO(getWalletEntity(affiliateId));
    }

    @Override
    @Transactional
    public AffiliateWallet getWalletEntity(Long affiliateId) {
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));

        return walletRepository.findByAffiliate(affiliate)
                .orElseGet(() -> createWallet(affiliate));
    }

    private AffiliateWallet createWallet(Affiliate affiliate) {
        return walletRepository.save(
                AffiliateWallet.builder()
                        .affiliate(affiliate)
                        .availableBalance(BigDecimal.ZERO)
                        .lockedBalance(BigDecimal.ZERO)
                        .totalEarned(BigDecimal.ZERO)
                        .totalPaid(BigDecimal.ZERO)
                        .build());
    }

    // ================= FINANCIAL OPERATIONS =================

    @Override
    @Transactional
    public void creditCommission(Long affiliateId, BigDecimal amount, Long saleId, String desc) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid credit amount");
        }

        if (saleId != null && transactionRepository.existsBySaleId(saleId)) {
            log.info("Already credited sale {}", saleId);
            return;
        }

        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));

        AffiliateWallet wallet = getWalletEntity(affiliateId);

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        wallet.setTotalEarned(wallet.getTotalEarned().add(amount));

        walletRepository.save(wallet);

        transactionRepository.save(
                AffiliateWalletTransaction.builder()
                        .affiliate(affiliate)
                        .type(AffiliateWalletTransaction.TransactionType.CREDIT)
                        .amount(amount)
                        .saleId(saleId)
                        .description(desc)
                        .build());
    }

    @Override
    @Transactional
    public void requestPayout(Long userId, BigDecimal amount) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));

        AffiliateWallet wallet = getWalletEntity(affiliate.getId());

        // 1. Fetch Global Config
        WalletConfig config = walletConfigRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("WalletConfig not found. System is currently disabled."));

        // 2. Business Rules Validation
        if (affiliate.getType() == AffiliateType.STUDENT && !config.isStudentWithdrawalEnabled()) {
            throw new IllegalStateException("Student payouts are currently disabled");
        }
        if (!config.isAffiliateWithdrawalEnabled()) {
            throw new IllegalStateException("Payout requests are globally disabled");
        }

        // 3. Amount Validations
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        BigDecimal minPayout = config.getDefaultMinPayoutAmount();
        if (affiliate.getMinPayout() != null && affiliate.getMinPayout().compareTo(BigDecimal.ZERO) > 0) {
            minPayout = affiliate.getMinPayout();
        }

        if (amount.compareTo(minPayout) < 0) {
            throw new IllegalArgumentException("Amount must be at least " + minPayout);
        }

        if (config.getMaxPayoutAmount() != null && amount.compareTo(config.getMaxPayoutAmount()) > 0) {
            throw new IllegalArgumentException("Amount exceeds maximum limit of " + config.getMaxPayoutAmount());
        }

        // 4. Pending Check
        long pendingCount = payoutRepository.findByAffiliateAndStatus(affiliate, AffiliatePayout.PayoutStatus.PENDING)
                .size();
        if (pendingCount >= config.getMaxPendingPayouts()) {
            throw new IllegalStateException("Maximum pending payout requests reached.");
        }

        // 5. Balance Check
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient available balance");
        }

        // 6. Execute Lock
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setLockedBalance(wallet.getLockedBalance().add(amount));
        walletRepository.save(wallet);

        payoutRepository.save(
                AffiliatePayout.builder()
                        .affiliate(affiliate)
                        .amount(amount)
                        .status(AffiliatePayout.PayoutStatus.PENDING)
                        .build());

        log.info("Payout requested successfully: user={}, amount={}", userId, amount);
    }

    @Override
    @Transactional
    public void approvePayout(Long payoutId) {
        AffiliatePayout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new IllegalArgumentException("Payout not found"));

        if (payout.getStatus() != AffiliatePayout.PayoutStatus.PENDING) {
            throw new IllegalStateException("Payout already processed");
        }

        AffiliateWallet wallet = getWalletEntity(payout.getAffiliate().getId());

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(payout.getAmount()));
        wallet.setTotalPaid(wallet.getTotalPaid().add(payout.getAmount()));
        walletRepository.save(wallet);

        payout.setStatus(AffiliatePayout.PayoutStatus.PAID);
        payout.setPaidAt(LocalDateTime.now());
        payoutRepository.save(payout);

        log.info("Payout approved: id={}, amount={}", payoutId, payout.getAmount());
    }

    @Override
    @Transactional
    public void rejectPayout(Long payoutId, String reason) {
        AffiliatePayout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new IllegalArgumentException("Payout not found"));

        if (payout.getStatus() != AffiliatePayout.PayoutStatus.PENDING) {
            throw new IllegalStateException("Payout already processed");
        }

        AffiliateWallet wallet = getWalletEntity(payout.getAffiliate().getId());

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(payout.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(payout.getAmount()));
        walletRepository.save(wallet);

        payout.setStatus(AffiliatePayout.PayoutStatus.FAILED);
        payout.setPaymentReference("REJECTED: " + reason);
        payoutRepository.save(payout);

        log.info("Payout rejected: id={}, reason={}", payoutId, reason);
    }

    // ================= CONFIGURATION =================

    @Override
    @Transactional(readOnly = true)
    public WalletConfigDTO getWalletConfig() {
        WalletConfig config = walletConfigRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("WalletConfig not found"));
        return mapToConfigDTO(config);
    }

    @Override
    @Transactional
    public WalletConfigDTO updateWalletConfig(WalletConfigDTO dto) {
        WalletConfig config = walletConfigRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> new WalletConfig());

        config.setDefaultMinPayoutAmount(dto.getDefaultMinPayoutAmount());
        config.setMaxPayoutAmount(dto.getMaxPayoutAmount());
        config.setStudentWithdrawalEnabled(dto.isStudentWithdrawalEnabled());
        config.setAffiliateWithdrawalEnabled(dto.isAffiliateWithdrawalEnabled());
        config.setMaxPendingPayouts(dto.getMaxPendingPayouts());

        return mapToConfigDTO(walletConfigRepository.save(config));
    }

    // ================= SECURE AFFILIATE METHODS =================

    @Override
    @Transactional(readOnly = true)
    public AffiliateWalletDTO getMyWalletSecure() {
        return getWalletByUserId(userContext.getCurrentUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliateTransactionDTO> getMyTransactionsSecure() {
        Long userId = userContext.getCurrentUserId();
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));

        return transactionRepository.findByAffiliateOrderByCreatedAtDesc(affiliate).stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void requestPayoutSecure(BigDecimal amount) {
        requestPayout(userContext.getCurrentUserId(), amount);
    }

    @Override
    @Transactional(readOnly = true)
    public AffiliateWalletDTO getWalletByUserId(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));
        return mapToWalletDTO(getWalletEntity(affiliate.getId()));
    }

    // ================= HELPERS =================

    private AffiliateWalletDTO mapToWalletDTO(AffiliateWallet wallet) {
        return AffiliateWalletDTO.builder()
                .availableBalance(wallet.getAvailableBalance())
                .lockedBalance(wallet.getLockedBalance())
                .totalEarned(wallet.getTotalEarned())
                .totalPaid(wallet.getTotalPaid())
                .build();
    }

    private AffiliateTransactionDTO mapToTransactionDTO(AffiliateWalletTransaction t) {
        return AffiliateTransactionDTO.builder()
                .id(t.getId())
                .type(t.getType().name())
                .amount(t.getAmount())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }

    private WalletConfigDTO mapToConfigDTO(WalletConfig config) {
        return WalletConfigDTO.builder()
                .defaultMinPayoutAmount(config.getDefaultMinPayoutAmount())
                .maxPayoutAmount(config.getMaxPayoutAmount())
                .studentWithdrawalEnabled(config.isStudentWithdrawalEnabled())
                .affiliateWithdrawalEnabled(config.isAffiliateWithdrawalEnabled())
                .maxPendingPayouts(config.getMaxPendingPayouts())
                .build();
    }
}