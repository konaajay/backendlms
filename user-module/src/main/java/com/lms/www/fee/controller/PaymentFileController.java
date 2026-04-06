package com.lms.www.fee.controller;

import com.lms.www.fee.dto.FileUploadResponse;
import com.lms.www.fee.service.PaymentFileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/fee-management/payments/files")
@RequiredArgsConstructor
public class PaymentFileController {

    private final PaymentFileService fileService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('PAYMENT_FILE_UPLOAD')")
    public ResponseEntity<FileUploadResponse> upload(@RequestParam MultipartFile file) {

        FileUploadResponse response = fileService.upload(file);

        return ResponseEntity.ok(response);
    }
}