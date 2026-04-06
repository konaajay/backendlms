package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.Vendor;
import com.lms.www.management.service.VendorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('VENDOR_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Vendor> createVendor(@RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.createVendor(vendor));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('VENDOR_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('VENDOR_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Vendor> getVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('VENDOR_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Vendor> updateVendor(
            @PathVariable Long id,
            @RequestBody Vendor vendor) {

        return ResponseEntity.ok(vendorService.updateVendor(id, vendor));
    }

    // =========================
    // FILE UPLOAD (NEW API)
    // =========================
    @PutMapping(value = "/{id}/documents", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('VENDOR_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Vendor> uploadVendorDocuments(
            @PathVariable Long id,
            @RequestPart(required = false) MultipartFile gstCertificate,
            @RequestPart(required = false) MultipartFile bankProof,
            @RequestPart(required = false) MultipartFile agreement) {

        return ResponseEntity.ok(
                vendorService.uploadVendorDocuments(id, gstCertificate, bankProof, agreement));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('VENDOR_DELETE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteVendor(@PathVariable Long id) {

        vendorService.deleteVendor(id);
        return ResponseEntity.ok("Vendor deactivated successfully");
    }

    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasAnyAuthority('VENDOR_DELETE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> hardDelete(@PathVariable Long id) {
        vendorService.hardDeleteVendor(id);
        return ResponseEntity.ok("Vendor permanently deleted");
    }
}