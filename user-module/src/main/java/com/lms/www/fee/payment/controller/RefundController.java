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

    @PostMapping("/request")
    @PreAuthorize("hasAuthority('REFUND_CREATE')")
    public ResponseEntity<RefundResponse> createRefund(@Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(refundService.createRefund(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('REFUND_VIEW_SELF')")
    public ResponseEntity<List<RefundResponse>> getMyRefunds() {
        return ResponseEntity.ok(refundService.getMyRefunds());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('REFUND_VIEW')")
    public ResponseEntity<List<RefundResponse>> getAllRefundRequests() {
        return ResponseEntity.ok(refundService.getAllRefundRequests());
    }

    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("hasAuthority('REFUND_VIEW')")
    public ResponseEntity<List<RefundResponse>> getRefundsByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(refundService.getRefundsByAllocation(allocationId));
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('REFUND_APPROVE')")
    public ResponseEntity<RefundResponse> approveRefund(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.approveRefund(id));
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('REFUND_REJECT')")
    public ResponseEntity<RefundResponse> rejectRefund(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(refundService.rejectRefund(id, reason));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REFUND_DELETE')")
    public ResponseEntity<Void> deleteRefundRequest(@PathVariable Long id) {
        refundService.deleteRefundRequest(id);
        return ResponseEntity.noContent().build();
    }
}
