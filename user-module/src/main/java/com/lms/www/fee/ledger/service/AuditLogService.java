package com.lms.www.fee.ledger.service;

import com.lms.www.fee.dto.FeeAuditLogResponse;
import org.springframework.data.domain.Page;
import java.util.Optional;

public interface AuditLogService {
    void log(String module, String entityName, Long entityId, com.lms.www.fee.ledger.entity.FeeAuditLog.Action action, String oldValue, String newValue);
    Page<FeeAuditLogResponse> getAuditLogs(String module, String entityName, Long entityId, int page, int size);
    Optional<FeeAuditLogResponse> getById(Long id);
}
