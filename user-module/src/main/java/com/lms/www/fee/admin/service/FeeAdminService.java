package com.lms.www.fee.admin.service;

import com.lms.www.fee.dto.MasterSettingRequest;
import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.RefundRuleRequest;
import com.lms.www.fee.dto.RefundRuleResponse;
import com.lms.www.fee.enums.MasterSettingType;
import java.util.List;

public interface FeeAdminService {
    void updateGlobalConfig(String key, String value);
    String getGlobalConfig(String key);
    
    MasterSettingResponse upsertMasterSetting(MasterSettingRequest request);
    List<MasterSettingResponse> getSettingsByType(MasterSettingType type);
    
    RefundRuleResponse createRefundRule(RefundRuleRequest request);
    List<RefundRuleResponse> getActiveRefundRules();
}
