package com.lms.www.fee.dto;

import com.lms.www.fee.enums.MasterSettingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterSettingRequest {
    private MasterSettingType type;
    private String key;
    private String value;
    private String description;
}
