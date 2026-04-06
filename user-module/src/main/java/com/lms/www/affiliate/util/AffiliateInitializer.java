package com.lms.www.affiliate.util;

import com.lms.www.affiliate.entity.WalletConfig;
import com.lms.www.affiliate.repository.WalletConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class AffiliateInitializer implements ApplicationRunner {

    private final WalletConfigRepository walletConfigRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (walletConfigRepository.count() == 0) {
            log.info("Initializing default WalletConfig...");
            WalletConfig config = WalletConfig.builder()
                    .defaultMinPayoutAmount(new BigDecimal("500.00"))
                    .maxPayoutAmount(new BigDecimal("50000.00"))
                    .studentWithdrawalEnabled(false)
                    .affiliateWithdrawalEnabled(true)
                    .maxPendingPayouts(1)
                    .build();
            walletConfigRepository.save(config);
            log.info("Default WalletConfig initialized successfully.");
        }
    }
}
