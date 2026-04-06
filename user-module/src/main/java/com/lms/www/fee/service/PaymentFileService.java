package com.lms.www.fee.service;

import com.lms.www.fee.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentFileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB

    public FileUploadResponse upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File too large");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only images allowed");
        }

        try {
            String original = Paths.get(file.getOriginalFilename())
                    .getFileName().toString();

            String fileName = UUID.randomUUID() + "_" + original;

            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            return new FileUploadResponse("/files/" + fileName);

        } catch (IOException e) {
            throw new RuntimeException("Upload failed");
        }
    }
}
