package com.lms.www.fee.penalty.service;

import com.lms.www.fee.dto.PenaltyResponse;
import com.lms.www.fee.dto.FeePenaltySlabRequest;
import com.lms.www.fee.dto.FeePenaltySlabResponse;
import java.math.BigDecimal;
import java.util.List;

public interface PenaltyService {
    // Penalty Transactions
    BigDecimal calculatePenalty(Long installmentId);
    PenaltyResponse applyPenalty(Long installmentId, BigDecimal amount, String reason);
    void waivePenalty(Long penaltyId);
    List<PenaltyResponse> getPenaltiesByInstallment(Long installmentId);
    
    // Penalty Configuration (Slabs)
    FeePenaltySlabResponse createPenaltySlab(FeePenaltySlabRequest request);
    List<FeePenaltySlabResponse> getAllPenaltySlabs();
    List<FeePenaltySlabResponse> getSlabsByFeeStructure(Long feeStructureId);
    FeePenaltySlabResponse updatePenaltySlab(Long id, FeePenaltySlabRequest request);
    void deletePenaltySlab(Long id);
    
    // General Settings (Migration from LateFeeService)
    FeePenaltySlabResponse getSlabById(Long id);
}
