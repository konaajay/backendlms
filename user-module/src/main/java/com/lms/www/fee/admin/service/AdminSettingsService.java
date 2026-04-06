package com.lms.www.fee.admin.service;

import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.MasterSettingsRequest;
import com.lms.www.fee.enums.MasterSettingType;
import java.util.List;

public interface AdminSettingsService {
    // Global Config (Old FeeAdminService & MasterSettingsService)
    void updateGlobalConfig(String key, String value);
    String getGlobalConfig(String key);
    String getGlobalSetting(String key, String defaultValue);

    // Master Settings (Old FeeAdminService & MasterSettingsService)
    MasterSettingResponse upsertSetting(MasterSettingType type, String key, String value, String description);
    List<MasterSettingResponse> getSettingsByType(MasterSettingType type);
    List<MasterSettingResponse> getSettingsByActive();
    void updateAllSettings(MasterSettingsRequest request);
    
    // System Helpers (Old SystemSettingService)
    List<Integer> getReminderOffsets();
    void setReminderOffsets(List<Integer> offsets);
    int getPaymentLinkDaysBeforeDue();
    void setPaymentLinkDaysBeforeDue(int days);
}
