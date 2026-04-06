package com.lms.www.fee.admin.service;

import com.lms.www.fee.admin.entity.GlobalConfig;
import com.lms.www.fee.admin.entity.MasterSetting;
import com.lms.www.fee.admin.entity.RefundRule;
import com.lms.www.fee.admin.repository.GlobalConfigRepository;
import com.lms.www.fee.admin.repository.MasterSettingRepository;
import com.lms.www.fee.admin.repository.RefundRuleRepository;
import com.lms.www.fee.dto.MasterSettingRequest;
import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.RefundRuleRequest;
import com.lms.www.fee.dto.RefundRuleResponse;
import com.lms.www.fee.enums.MasterSettingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FeeAdminServiceImpl implements FeeAdminService {

    private final GlobalConfigRepository globalConfigRepository;
    private final MasterSettingRepository masterSettingRepository;
    private final RefundRuleRepository refundRuleRepository;

    @Override
    public void updateGlobalConfig(String key, String value) {
        GlobalConfig config = globalConfigRepository.findById(key)
                .orElse(new GlobalConfig(key, value, null));
        config.setConfigValue(value);
        globalConfigRepository.save(config);
    }

    @Override
    public String getGlobalConfig(String key) {
        return globalConfigRepository.findById(key)
                .map(GlobalConfig::getConfigValue)
                .orElseThrow(() -> new RuntimeException("Config not found"));
    }

    @Override
    public MasterSettingResponse upsertMasterSetting(MasterSettingRequest request) {
        MasterSetting setting = masterSettingRepository.findByTypeAndKeyName(request.getType(), request.getKey())
                .orElse(new MasterSetting());
        setting.setType(request.getType());
        setting.setKeyName(request.getKey());
        setting.setValue(request.getValue());
        setting.setDescription(request.getDescription());
        setting.setActive(true);
        return mapToResponse(masterSettingRepository.save(setting));
    }

    @Override
    public List<MasterSettingResponse> getSettingsByType(MasterSettingType type) {
        return masterSettingRepository.findByTypeAndActiveTrue(type).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public RefundRuleResponse createRefundRule(RefundRuleRequest request) {
        RefundRule rule = RefundRule.builder()
                .ruleName(request.getName())
                .daysBeforeStart(request.getDaysBeforeStart())
                .refundPercentage(request.getRefundPercentage())
                .active(true)
                .build();
        return mapToResponse(refundRuleRepository.save(rule));
    }

    @Override
    public List<RefundRuleResponse> getActiveRefundRules() {
        return refundRuleRepository.findByActiveTrue().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private MasterSettingResponse mapToResponse(MasterSetting setting) {
        if (setting == null) return null;
        return MasterSettingResponse.builder()
                .id(setting.getId())
                .type(setting.getType() != null ? setting.getType().name() : null)
                .key(setting.getKeyName())
                .keyName(setting.getKeyName())
                .value(setting.getValue())
                .description(setting.getDescription())
                .active(setting.isActive())
                .build();
    }

    private RefundRuleResponse mapToResponse(RefundRule rule) {
        if (rule == null) return null;
        return RefundRuleResponse.builder()
                .id(rule.getId())
                .name(rule.getRuleName())
                .daysBeforeStart(rule.getDaysBeforeStart())
                .refundPercentage(rule.getRefundPercentage())
                .active(rule.getActive())
                .build();
    }
}
