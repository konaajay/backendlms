package com.lms.www.affiliate.service;

import java.math.BigDecimal;
import java.util.List;

import com.lms.www.affiliate.dto.AffiliateAdminSaleResponse;
import com.lms.www.affiliate.dto.AffiliateSaleDTO;
import com.lms.www.affiliate.entity.AffiliateSale;

public interface SaleService {
    AffiliateSale convertLeadToEnrollment(Long leadId, Long studentId, BigDecimal batchPrice);

    void approveCommission(Long saleId, String approvedBy);

    void markSaleAsPaid(Long saleId, String referenceId);

    void cancelSale(Long saleId, String reason);

    List<AffiliateAdminSaleResponse> getAllSales();

    List<AffiliateSaleDTO> getSalesByUserId(Long userId);

    List<AffiliateSaleDTO> getSalesSecure();

    String exportSalesCsv();
}
