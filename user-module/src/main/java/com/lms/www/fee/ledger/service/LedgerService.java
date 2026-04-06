package com.lms.www.fee.ledger.service;

import com.lms.www.fee.ledger.entity.FeeAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LedgerService {
    void logAction(String module, Long entityId, String entityName, FeeAuditLog.Action action, String oldValue, String newValue);
    Page<FeeAuditLog> getLogs(String module, String entityName, Long entityId, Pageable pageable);
}
