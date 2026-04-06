package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeAuditLogResponse {
    private Long id;
    private String module;
    private String entityName;
    private Long entityId;
    private String action;
    private String oldValue;
    private String newValue;
    private Long performedBy;
    private String ipAddress;
    private LocalDateTime performedAt;
}