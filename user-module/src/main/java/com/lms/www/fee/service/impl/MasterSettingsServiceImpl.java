package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.MasterSettingsRequest;
import com.lms.www.fee.admin.entity.MasterSetting;
import com.lms.www.fee.admin.repository.MasterSettingRepository;
import com.lms.www.fee.service.MasterSettingsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MasterSettingsServiceImpl implements MasterSettingsService {

    private final MasterSettingRepository repo;

    @Override
    public MasterSettingResponse saveGlobalConfig(String key, String value) {
        MasterSetting setting = repo.findByKeyName(key)
                .orElse(new MasterSetting());
        setting.setKeyName(key);
        setting.setValue(value);
        setting.setActive(true);
        return FeeMapper.toResponse(repo.save(setting));
    }

    @Override
    public List<MasterSettingResponse> getAllGlobalConfigs() {
        return repo.findAll().stream()
                .map(s -> FeeMapper.toResponse(s))
                .collect(Collectors.toList());
    }

    @Override
    public String getGlobalSetting(String key, String defaultValue) {
        return repo.findByKeyName(key)
                .map(MasterSetting::getValue)
                .orElse(defaultValue);
    }

    @Override
    public String getSettingValue(com.lms.www.fee.enums.MasterSettingType type, String key, String defaultValue) {
        return repo.findByTypeAndKeyNameAndActiveTrue(type, key)
                .map(MasterSetting::getValue)
                .orElse(defaultValue);
    }

    @Override
    public void updateAllSettings(MasterSettingsRequest request) {
        if (request == null) return;

        // General
        if (request.getGeneral() != null) {
            saveSetting("general_currency", request.getGeneral().getCurrency());
            saveSetting("general_tax_percentage", String.valueOf(request.getGeneral().getTaxPercentage()));
            saveSetting("general_invoice_prefix", request.getGeneral().getInvoicePrefix());
        }

        // Penalty
        if (request.getPenalty() != null) {
            saveSetting("penalty_enabled", String.valueOf(request.getPenalty().getEnabled()));
            saveSetting("penalty_amount", String.valueOf(request.getPenalty().getAmount()));
            saveSetting("penalty_frequency", request.getPenalty().getFrequency());
        }

        // Notifications
        if (request.getNotifications() != null) {
            saveSetting("notification_creation", String.valueOf(request.getNotifications().getCreation()));
            saveSetting("notification_payment_success", String.valueOf(request.getNotifications().getPaymentSuccess()));
        }
    }

    private void saveSetting(String key, String value) {
        if (value == null) return;
        MasterSetting setting = repo.findByKeyName(key)
                .orElse(MasterSetting.builder().keyName(key).active(true).build());
        setting.setValue(value);
        repo.save(setting);
    }

    @Override
    public List<MasterSettingResponse> getAllSettings() {
        return repo.findByActiveTrue().stream()
                .map(s -> FeeMapper.toResponse(s))
                .collect(Collectors.toList());
    }
}
