package com.lms.www.fee.service;

import com.lms.www.fee.dto.FeeAuditLogResponse;
import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.ledger.repository.FeeAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeeAuditLogService {

    private final FeeAuditLogRepository repository;

    public Page<FeeAuditLogResponse> getAuditLogs(
            String module,
            String entityName,
            Long entityId,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(size, 50),
                Sort.by(Sort.Direction.DESC, "performedAt")
        );

        Page<FeeAuditLog> logs = repository.search(module, entityName, entityId, pageable);

        return logs.map(this::mapToDto);
    }

    public Optional<FeeAuditLogResponse> getById(Long id) {
        return repository.findById(id).map(this::mapToDto);
    }

    private FeeAuditLogResponse mapToDto(FeeAuditLog log) {
        return FeeAuditLogResponse.builder()
                .id(log.getId())
                .module(log.getModule())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .action(log.getAction().name())
                .performedBy(log.getPerformedBy())
                .performedAt(log.getPerformedAt())
                .build();
    }
}
