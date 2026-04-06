package com.lms.www.fee.ledger.controller;

import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fee-management/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('LEDGER_VIEW')")
    public ResponseEntity<Page<FeeAuditLog>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) Long entityId,
            Pageable pageable) {
        return ResponseEntity.ok(ledgerService.getLogs(module, entityName, entityId, pageable));
    }
}
