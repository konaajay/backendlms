package com.lms.www.fee.ledger.controller;

import com.lms.www.fee.dto.FeeAuditLogResponse;
import com.lms.www.fee.ledger.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fee-management/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_LOG_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Page<FeeAuditLogResponse>> getAll(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FeeAuditLogResponse> response = auditLogService.getAuditLogs(module, entityName, entityId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_LOG_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<FeeAuditLogResponse> getById(@PathVariable Long id) {
        return auditLogService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Audit log not found"));
    }
}
