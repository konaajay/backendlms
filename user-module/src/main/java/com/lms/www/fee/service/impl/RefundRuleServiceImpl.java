package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.RefundRuleRequest;
import com.lms.www.fee.dto.RefundRuleResponse;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.security.UserContext;
import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.fee.admin.entity.RefundRule;
import com.lms.www.fee.admin.repository.RefundRuleRepository;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.service.RefundRuleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundRuleServiceImpl implements RefundRuleService {

    private final RefundRuleRepository ruleRepository;
    private final StudentFeeAllocationRepository allocationRepository;
    private final UserContext userContext;

    @Override
    public RefundRuleResponse createRule(RefundRuleRequest request) {
        RefundRule rule = FeeMapper.toEntity(request);
        return FeeMapper.toResponse(ruleRepository.save(rule));
    }

    @Override
    public List<RefundRuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(rule -> FeeMapper.toResponse(rule))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    @Override
    public RefundRuleResponse getRuleById(Long id) {
        return ruleRepository.findById(id)
                .map(rule -> FeeMapper.toResponse(rule))
                .orElseThrow(() -> new ResourceNotFoundException("Refund rule not found with id: " + id));
    }

    @Override
    public RefundRuleResponse getRuleSecure(Long id) {
        return getRuleById(id);
    }

    @Override
    public RefundRuleResponse updateRule(Long id, RefundRuleRequest request) {
        RefundRule existingRule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund rule not found with id: " + id));
        
        existingRule.setRuleName(request.getName());
        existingRule.setDaysBeforeStart(request.getDaysBeforeStart());
        existingRule.setRefundPercentage(request.getRefundPercentage());
        existingRule.setActive(request.getActive());
        
        return FeeMapper.toResponse(ruleRepository.save(existingRule));
    }

    @Override
    public BigDecimal calculateRefundAmount(Long allocationId) {
        StudentFeeAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found"));

        BigDecimal paidAmount = allocation.getPayableAmount().subtract(allocation.getRemainingAmount());

        if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        List<RefundRule> rules = ruleRepository.findByActiveTrue();
        if (rules.isEmpty()) {
            return paidAmount;
        }

        // Logic for finding applicable rule based on days
        return paidAmount;
    }
}
