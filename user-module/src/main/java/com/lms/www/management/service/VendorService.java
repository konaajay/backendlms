package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.Vendor;

import org.springframework.web.multipart.MultipartFile;

public interface VendorService {

    Vendor createVendor(Vendor vendor);

    List<Vendor> getAllVendors();

    Vendor getVendorById(Long id);

    Vendor updateVendor(Long id, Vendor vendor);

    void deleteVendor(Long id);

    Vendor uploadVendorDocuments(Long id, MultipartFile gstCertificate, MultipartFile bankProof, MultipartFile agreement);

    void hardDeleteVendor(Long id);

}