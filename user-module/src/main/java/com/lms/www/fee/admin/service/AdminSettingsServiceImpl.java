package com.lms.www.fee.admin.service;

import com.lms.www.fee.admin.entity.SystemSetting;
import com.lms.www.fee.admin.repository.SystemSettingRepository;
import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.MasterSettingsRequest;
import com.lms.www.fee.enums.MasterSettingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminSettingsServiceImpl implements AdminSettingsService {

    private final SystemSettingRepository repository;

    private static final String KEY_REMINDER_OFFSETS = "payment.link.reminder_days";
    private static final String DEFAULT_OFFSETS = "-3,0,2";

    @Override
    public void updateGlobalConfig(String key, String value) {
        java.util.Objects.requireNonNull(key, "Key cannot be null");
        SystemSetting setting = repository.findById(key)
                .orElse(SystemSetting.builder().key(key).type(MasterSettingType.SYSTEM).build());
        setting.setValue(value);
        repository.save(setting);
    }

    @Override
    public String getGlobalConfig(String key) {
        java.util.Objects.requireNonNull(key, "Key cannot be null");
        return repository.findById(key)
                .map(SystemSetting::getValue)
                .orElseThrow(() -> new RuntimeException("Config not found: " + key));
    }

    @Override
    public String getGlobalSetting(String key, String defaultValue) {
        java.util.Objects.requireNonNull(key, "Key cannot be null");
        return repository.findById(key)
                .map(SystemSetting::getValue)
                .orElse(defaultValue);
    }

    @Override
    public MasterSettingResponse upsertSetting(MasterSettingType type, String key, String value, String description) {
        SystemSetting setting = repository.findByTypeAndKey(type, key)
                .orElse(SystemSetting.builder().type(type).key(key).build());
        
        setting.setValue(value);
        setting.setDescription(description);
        setting.setActive(true);
        
        return mapToResponse(repository.save(setting));
    }

    @Override
    public List<MasterSettingResponse> getSettingsByType(MasterSettingType type) {
        return repository.findByTypeAndActiveTrue(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAllSettings(MasterSettingsRequest request) {
        if (request == null) return;

        // General
        if (request.getGeneral() != null) {
            saveSetting(MasterSettingType.GENERAL, "general_currency", request.getGeneral().getCurrency());
            saveSetting(MasterSettingType.GENERAL, "general_tax_percentage", String.valueOf(request.getGeneral().getTaxPercentage()));
            saveSetting(MasterSettingType.GENERAL, "general_invoice_prefix", request.getGeneral().getInvoicePrefix());
        }

        // Penalty
        if (request.getPenalty() != null) {
            saveSetting(MasterSettingType.LATE_FEE, "penalty_enabled", String.valueOf(request.getPenalty().getEnabled()));
            saveSetting(MasterSettingType.LATE_FEE, "penalty_amount", String.valueOf(request.getPenalty().getAmount()));
            saveSetting(MasterSettingType.LATE_FEE, "penalty_frequency", request.getPenalty().getFrequency());
        }

        // Notifications
        if (request.getNotifications() != null) {
            saveSetting(MasterSettingType.SYSTEM, "notification_creation", String.valueOf(request.getNotifications().getCreation()));
            saveSetting(MasterSettingType.SYSTEM, "notification_payment_success", String.valueOf(request.getNotifications().getPaymentSuccess()));
        }
    }

    private void saveSetting(MasterSettingType type, String key, String value) {
        if (value == null) return;
        SystemSetting setting = repository.findByTypeAndKey(type, key)
                .orElse(SystemSetting.builder().type(type).key(key).active(true).build());
        setting.setValue(value);
        repository.save(setting);
    }

    @Override
    public List<MasterSettingResponse> getSettingsByActive() {
        return repository.findAll().stream()
                .filter(SystemSetting::isActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Refund rules are now handled by RefundRuleService
    
    // System Helpers (Moved from SystemSettingService)

    // System Helpers (Moved from SystemSettingService)
    @Override
    public List<Integer> getReminderOffsets() {
        String csv = repository.findByTypeAndKey(MasterSettingType.SYSTEM, KEY_REMINDER_OFFSETS)
                .map(SystemSetting::getValue)
                .orElse(DEFAULT_OFFSETS);
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    @Override
    public void setReminderOffsets(List<Integer> offsets) {
        String csv = offsets.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        SystemSetting setting = repository.findByTypeAndKey(MasterSettingType.SYSTEM, KEY_REMINDER_OFFSETS)
                .orElse(SystemSetting.builder().type(MasterSettingType.SYSTEM).key(KEY_REMINDER_OFFSETS).build());

        setting.setValue(csv);
        setting.setDescription("Comma-separated reminder offsets (e.g., -3,0,2)");
        setting.setActive(true);
        repository.save(setting);
    }

    @Override
    public void setPaymentLinkDaysBeforeDue(int days) {
        setReminderOffsets(List.of(-days, 0));
    }

    @Override
    public int getPaymentLinkDaysBeforeDue() {
        return getReminderOffsets().stream()
                .filter(o -> o < 0)
                .mapToInt(Integer::intValue)
                .map(Math::abs)
                .findFirst()
                .orElse(3);
    }

    private MasterSettingResponse mapToResponse(SystemSetting setting) {
        if (setting == null) return null;
        return MasterSettingResponse.builder()
                .id(0L) // SystemSetting uses String PK, returning 0 as Long for compatibility if needed
                .type(setting.getType() != null ? setting.getType().name() : null)
                .key(setting.getKey())
                .keyName(setting.getKey())
                .value(setting.getValue())
                .description(setting.getDescription())
                .active(setting.isActive())
                .build();
    }
}
