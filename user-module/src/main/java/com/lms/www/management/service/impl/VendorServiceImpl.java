package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.Vendor;
import com.lms.www.management.repository.VendorRepository;
import com.lms.www.management.service.FileStorageService;
import com.lms.www.management.service.VendorService;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Vendor createVendor(Vendor vendor) {

        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setStatus("ACTIVE");

        return vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }

    @Override
    public Vendor updateVendor(Long id, Vendor vendor) {

        Vendor existing = getVendorById(id);

        if (vendor.getVendorName() != null) existing.setVendorName(vendor.getVendorName());
        if (vendor.getContactPerson() != null) existing.setContactPerson(vendor.getContactPerson());
        if (vendor.getPhoneNumber() != null) existing.setPhoneNumber(vendor.getPhoneNumber());
        if (vendor.getEmail() != null) existing.setEmail(vendor.getEmail());
        if (vendor.getVendorType() != null) existing.setVendorType(vendor.getVendorType());
        if (vendor.getStatus() != null) existing.setStatus(vendor.getStatus());
        
        // Address
        if (vendor.getStreetAddress() != null) existing.setStreetAddress(vendor.getStreetAddress());
        if (vendor.getCity() != null) existing.setCity(vendor.getCity());
        if (vendor.getState() != null) existing.setState(vendor.getState());
        if (vendor.getPincode() != null) existing.setPincode(vendor.getPincode());
        
        // Business Info
        if (vendor.getGstNumber() != null) existing.setGstNumber(vendor.getGstNumber());
        if (vendor.getPanNumber() != null) existing.setPanNumber(vendor.getPanNumber());
        
        // Bank Details
        if (vendor.getBankName() != null) existing.setBankName(vendor.getBankName());
        if (vendor.getAccountNumber() != null) existing.setAccountNumber(vendor.getAccountNumber());
        if (vendor.getIfscCode() != null) existing.setIfscCode(vendor.getIfscCode());
        if (vendor.getBranchName() != null) existing.setBranchName(vendor.getBranchName());
        if (vendor.getPaymentTerms() != null) existing.setPaymentTerms(vendor.getPaymentTerms());

        return vendorRepository.save(existing);
    }

    @Override
    public void deleteVendor(Long id) {

        Vendor vendor = getVendorById(id);
        vendor.setStatus("INACTIVE");

        vendorRepository.save(vendor);
    }

    @Override
    public Vendor uploadVendorDocuments(Long id, MultipartFile gstCertificate, MultipartFile bankProof, MultipartFile agreement) {
        Vendor vendor = getVendorById(id);

        if (gstCertificate != null && !gstCertificate.isEmpty()) {
            vendor.setGstCertificatePath(fileStorageService.storeFile(gstCertificate));
        }
        if (bankProof != null && !bankProof.isEmpty()) {
            vendor.setBankProofPath(fileStorageService.storeFile(bankProof));
        }
        if (agreement != null && !agreement.isEmpty()) {
            vendor.setAgreementPath(fileStorageService.storeFile(agreement));
        }

        return vendorRepository.save(vendor);
    }

    @Override
    public void hardDeleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }
}