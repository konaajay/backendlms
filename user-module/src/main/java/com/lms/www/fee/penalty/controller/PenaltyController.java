package com.lms.www.fee.penalty.controller;

import com.lms.www.fee.dto.PenaltyResponse;
import com.lms.www.fee.dto.FeePenaltySlabRequest;
import com.lms.www.fee.dto.FeePenaltySlabResponse;
import com.lms.www.fee.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/penalty")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    // --- Penalty Transactions ---

    @GetMapping("/calculate/{installmentId}")
    @PreAuthorize("hasAuthority('PENALTY_VIEW')")
    public ResponseEntity<BigDecimal> calculatePenalty(@PathVariable Long installmentId) {
        return ResponseEntity.ok(penaltyService.calculatePenalty(installmentId));
    }

    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('PENALTY_APPLY')")
    public ResponseEntity<PenaltyResponse> applyPenalty(
            @RequestParam Long installmentId,
            @RequestParam BigDecimal amount,
            @RequestParam String reason) {
        return ResponseEntity.ok(penaltyService.applyPenalty(installmentId, amount, reason));
    }

    @PostMapping("/waive/{id}")
    @PreAuthorize("hasAuthority('PENALTY_WAIVE')")
    public ResponseEntity<Void> waivePenalty(@PathVariable Long id) {
        penaltyService.waivePenalty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/installment/{installmentId}")
    @PreAuthorize("hasAuthority('PENALTY_VIEW')")
    public ResponseEntity<List<PenaltyResponse>> getPenaltiesByInstallment(@PathVariable Long installmentId) {
        return ResponseEntity.ok(penaltyService.getPenaltiesByInstallment(installmentId));
    }

    // --- Penalty Configurations (Slabs) ---

    @PostMapping("/config/slab")
    @PreAuthorize("hasAuthority('PENALTY_CONFIG_UPDATE')")
    public ResponseEntity<FeePenaltySlabResponse> createSlab(@RequestBody FeePenaltySlabRequest request) {
        return ResponseEntity.ok(penaltyService.createPenaltySlab(request));
    }

    @GetMapping("/config/slabs")
    @PreAuthorize("hasAuthority('PENALTY_CONFIG_VIEW')")
    public ResponseEntity<List<FeePenaltySlabResponse>> getAllSlabs() {
        return ResponseEntity.ok(penaltyService.getAllPenaltySlabs());
    }

    @GetMapping("/config/structure/{structureId}")
    @PreAuthorize("hasAuthority('PENALTY_CONFIG_VIEW')")
    public ResponseEntity<List<FeePenaltySlabResponse>> getSlabsByStructure(@PathVariable Long structureId) {
        return ResponseEntity.ok(penaltyService.getSlabsByFeeStructure(structureId));
    }

    @PutMapping("/config/slab/{id}")
    @PreAuthorize("hasAuthority('PENALTY_CONFIG_UPDATE')")
    public ResponseEntity<FeePenaltySlabResponse> updateSlab(@PathVariable Long id, @RequestBody FeePenaltySlabRequest request) {
        return ResponseEntity.ok(penaltyService.updatePenaltySlab(id, request));
    }

    @DeleteMapping("/config/slab/{id}")
    @PreAuthorize("hasAuthority('PENALTY_CONFIG_DELETE')")
    public ResponseEntity<Void> deleteSlab(@PathVariable Long id) {
        penaltyService.deletePenaltySlab(id);
        return ResponseEntity.noContent().build();
    }
}
