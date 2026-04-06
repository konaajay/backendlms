package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterSettingResponse {
    private Long id;
    private String type;
    private String key;
    private String keyName;
    private String value;
    private String description;
    private boolean active;
}
