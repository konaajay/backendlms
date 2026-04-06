package com.lms.www.fee.ledger.service;

import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.ledger.repository.FeeAuditLogRepository;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LedgerServiceImpl implements LedgerService {

    private final FeeAuditLogRepository auditLogRepository;
    private final UserContext userContext;

    @Override
    public void logAction(String module, Long entityId, String entityName, FeeAuditLog.Action action, String oldValue, String newValue) {
        FeeAuditLog logEntry = FeeAuditLog.builder()
                .module(module)
                .entityId(entityId)
                .entityName(entityName)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .performedBy(userContext.getCurrentUserId())
                .build();
        auditLogRepository.save(logEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeeAuditLog> getLogs(String module, String entityName, Long entityId, Pageable pageable) {
        return auditLogRepository.search(module, entityName, entityId, pageable);
    }
}
