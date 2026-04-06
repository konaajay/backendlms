package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.lms.www.management.model.DigitalAsset;
import com.lms.www.management.service.DigitalAssetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/digital-assets")
@RequiredArgsConstructor
public class DigitalAssetController {

    private final DigitalAssetService digitalAssetService;

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasAnyAuthority('DIGITAL_ASSET_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DigitalAsset> createAsset(@RequestBody DigitalAsset asset) {

        return ResponseEntity.status(201)
                .body(digitalAssetService.createAsset(asset));
    }

    // ✅ GET ALL
    @GetMapping
    @PreAuthorize("hasAnyAuthority('DIGITAL_ASSET_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<DigitalAsset>> getAllAssets() {
        return ResponseEntity.ok(digitalAssetService.getAllAssets());
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('DIGITAL_ASSET_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DigitalAsset> getAsset(@PathVariable Long id) {
        return ResponseEntity.ok(digitalAssetService.getAssetById(id));
    }

    // ✅ ASSIGN LICENSE
    @PutMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAnyAuthority('DIGITAL_ASSET_ASSIGN', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DigitalAsset> assignLicense(
            @PathVariable Long id,
            @PathVariable Long userId) {

        return ResponseEntity.ok(digitalAssetService.assignLicense(id, userId));
    }

    // ✅ RELEASE LICENSE (NEW)
    @PutMapping("/{id}/release")
    @PreAuthorize("hasAnyAuthority('DIGITAL_ASSET_ASSIGN', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<DigitalAsset> releaseLicense(@PathVariable Long id) {

        return ResponseEntity.ok(digitalAssetService.releaseLicense(id));
    }

    @PreAuthorize("hasAuthority('DIGITAL_ASSET_ASSIGN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAsset(@PathVariable Long id) {
        digitalAssetService.deleteAsset(id);
        return ResponseEntity.ok("Digital Asset deleted successfully");
    }
}