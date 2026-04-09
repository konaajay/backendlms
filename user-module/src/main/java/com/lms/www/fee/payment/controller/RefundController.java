package com.lms.www.fee.payment.controller;

import com.lms.www.fee.dto.RefundRequest;
import com.lms.www.fee.dto.RefundResponse;
import com.lms.www.fee.payment.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('REFUND_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(refundService.createRefund(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('REFUND_VIEW_SELF', 'ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<RefundResponse>> getMyRefunds() {
        return ResponseEntity.ok(refundService.getMyRefunds());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('REFUND_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<RefundResponse>> getAllRefundRequests() {
        return ResponseEntity.ok(refundService.getAllRefundRequests());
    }

    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("hasAnyAuthority('REFUND_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<RefundResponse>> getRefundsByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(refundService.getRefundsByAllocation(allocationId));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('REFUND_APPROVE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<RefundResponse> approveRefund(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.approveRefund(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('REFUND_REJECT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<RefundResponse> rejectRefund(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(refundService.rejectRefund(id, reason));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('REFUND_DELETE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteRefundRequest(@PathVariable Long id) {
        refundService.deleteRefundRequest(id);
        return ResponseEntity.noContent().build();
    }
}
