package com.lms.www.fee.ledger.service.impl;

import com.lms.www.fee.dto.FeeAuditLogResponse;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.ledger.repository.FeeAuditLogRepository;
import com.lms.www.fee.ledger.service.AuditLogService;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final FeeAuditLogRepository feeAuditLogRepository;
    private final UserContext userContext;

    @Override
    public void log(String module, String entityName, Long entityId, FeeAuditLog.Action action, String oldValue, String newValue) {
        try {
            Long performedBy = 1L; // Default to Demo Admin
            try {
                performedBy = userContext.getCurrentUserId();
            } catch (Exception e) {
                // Fallback to demo user if context fails
            }

            FeeAuditLog auditLog = FeeAuditLog.builder()
                    .module(module)
                    .entityName(entityName)
                    .entityId(entityId)
                    .action(action)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .performedBy(performedBy)
                    .build();
            feeAuditLogRepository.save(java.util.Objects.requireNonNull(auditLog));
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to save Audit Log: " + e.getMessage());
        }
    }

    @Override
    public Page<FeeAuditLogResponse> getAuditLogs(String module, String entityName, Long entityId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 50),
                Sort.by(Sort.Direction.DESC, "performedAt"));

        return feeAuditLogRepository.search(module, entityName, entityId, pageable)
                .map(FeeMapper::toResponse);
    }

    @Override
    public Optional<FeeAuditLogResponse> getById(Long id) {
        return feeAuditLogRepository.findById(id)
                .map(FeeMapper::toResponse);
    }
}
