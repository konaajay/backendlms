package com.lms.www.affiliate.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.affiliate.entity.AffiliateSale;
import com.lms.www.affiliate.repository.AffiliateSaleRepository;
import com.lms.www.affiliate.service.PayoutJobService;
import com.lms.www.affiliate.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutJobServiceImpl implements PayoutJobService {

    private final AffiliateSaleRepository saleRepository;
    private final WalletService walletService;

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processDailyPayouts() {
        processDailyCommission();
    }

    @Transactional
    public void processDailyCommission() {
        log.info("[PayoutJob] Starting daily commission process...");

        List<AffiliateSale> sales = saleRepository.findByStatus(AffiliateSale.SaleStatus.APPROVED);

        if (sales.isEmpty()) {
            log.info("[PayoutJob] No approved sales to process.");
            return;
        }

        Map<Long, List<AffiliateSale>> grouped =
                sales.stream().collect(Collectors.groupingBy(s -> s.getAffiliate().getId()));

        grouped.forEach((affiliateId, saleList) -> {
            log.info("[PayoutJob] Processing commission for affiliate {}: sales={}", affiliateId, saleList.size());

            BigDecimal total = saleList.stream()
                    .map(s -> s.getCommissionAmount() == null ? BigDecimal.ZERO : s.getCommissionAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // ✅ CREDIT ONLY
            walletService.creditCommission(affiliateId, total, null, "Daily commission");

            // ✅ mark processed
            saleList.forEach(s -> s.setStatus(AffiliateSale.SaleStatus.CREDITED));
            saleRepository.saveAll(saleList);
        });

        log.info("[PayoutJob] Daily commission process finished.");
    }
}
