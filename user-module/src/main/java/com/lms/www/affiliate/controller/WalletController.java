package com.lms.www.affiliate.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.affiliate.dto.AffiliateTransactionDTO;
import com.lms.www.affiliate.dto.AffiliateWalletDTO;
import com.lms.www.affiliate.dto.PayoutRequestDTO;
import com.lms.www.affiliate.dto.WalletConfigDTO;
import com.lms.www.affiliate.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "https://yourdomain.com") // Secure CORS as requested
public class WalletController {

    private final WalletService walletService;

    // ================= ADMIN / DASHBOARD =================

    @GetMapping("/api/v1/admin/wallets")
    @PreAuthorize("hasAuthority('WALLET_VIEW_ALL') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AffiliateWalletDTO>> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @GetMapping("/api/v1/admin/wallets/{affiliateId}")
    @PreAuthorize("hasAuthority('WALLET_VIEW_ALL') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateWalletDTO> getWallet(@PathVariable Long affiliateId) {
        return ResponseEntity.ok(walletService.getWallet(affiliateId));
    }

    @GetMapping("/api/v1/admin/wallets/{affiliateId}/transactions")
    @PreAuthorize("hasAuthority('WALLET_VIEW_ALL') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AffiliateTransactionDTO>> getTransactions(
            @PathVariable Long affiliateId) {
        return ResponseEntity.ok(walletService.getWalletTransactions(affiliateId));
    }

    // ===== WALLET CONFIG =====

    @GetMapping("/api/v1/admin/wallet-config")
    @PreAuthorize("hasAuthority('WALLET_CONFIG_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<WalletConfigDTO> getWalletConfig() {
        return ResponseEntity.ok(walletService.getWalletConfig());
    }

    @PostMapping("/api/v1/admin/wallet-config")
    @PreAuthorize("hasAuthority('WALLET_CONFIG_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<WalletConfigDTO> updateWalletConfig(
            @Valid @RequestBody WalletConfigDTO configDTO) {
        log.info("Admin updated wallet config");
        return ResponseEntity.ok(walletService.updateWalletConfig(configDTO));
    }

    // ================= AFFILIATE PORTAL =================

    @GetMapping("/api/v1/affiliate/me/wallet")
    @PreAuthorize("hasAuthority('WALLET_VIEW_OWN') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateWalletDTO> getMyWallet() {
        return ResponseEntity.ok(walletService.getMyWalletSecure());
    }

    @PostMapping("/api/v1/affiliate/me/payout-request") // Renamed as requested
    @PreAuthorize("hasAuthority('WALLET_WITHDRAW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> requestPayout(@RequestBody @Valid PayoutRequestDTO dto) {
        log.info("Payout requested via controller for user amount={}", dto.getAmount());
        walletService.requestPayoutSecure(dto.getAmount());
        return ResponseEntity.ok("Payout request submitted successfully");
    }

    @GetMapping("/api/v1/affiliate/me/transactions")
    @PreAuthorize("hasAuthority('WALLET_VIEW_OWN') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AffiliateTransactionDTO>> getMyTransactions() {
        return ResponseEntity.ok(walletService.getMyTransactionsSecure());
    }
}