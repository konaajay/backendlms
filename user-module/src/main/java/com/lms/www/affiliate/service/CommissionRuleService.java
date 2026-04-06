package com.lms.www.affiliate.service;

import java.util.List;
import java.util.Optional;

import com.lms.www.affiliate.entity.CommissionRule;

public interface CommissionRuleService {
    List<CommissionRule> getAllRules();
    CommissionRule createRule(CommissionRule rule);
    Optional<CommissionRule> updateRule(Long id, CommissionRule ruleDetails);
    boolean deleteRule(Long id);
}
