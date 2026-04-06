package com.lms.www.fee.service;

import java.math.BigDecimal;
import java.util.List;

import com.lms.www.fee.dto.RefundRuleRequest;
import com.lms.www.fee.dto.RefundRuleResponse;

public interface RefundRuleService {
    RefundRuleResponse createRule(RefundRuleRequest request);

    List<RefundRuleResponse> getAllRules();

    void deleteRule(Long id);

    RefundRuleResponse getRuleById(Long id);

    RefundRuleResponse getRuleSecure(Long id);

    RefundRuleResponse updateRule(Long id, RefundRuleRequest request);

    BigDecimal calculateRefundAmount(Long allocationId);
}
