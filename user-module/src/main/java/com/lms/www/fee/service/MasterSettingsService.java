package com.lms.www.fee.service;

import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.MasterSettingsRequest;
import java.util.List;

public interface MasterSettingsService {
    MasterSettingResponse saveGlobalConfig(String key, String value);
    List<MasterSettingResponse> getAllGlobalConfigs();
    String getGlobalSetting(String key, String defaultValue);
    
    void updateAllSettings(MasterSettingsRequest request);
    List<MasterSettingResponse> getAllSettings();
    String getSettingValue(com.lms.www.fee.enums.MasterSettingType type, String key, String defaultValue);
}
