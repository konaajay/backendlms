package com.lms.www.management.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.exception.UnauthorizedAccessException;
import com.lms.www.management.model.TopicContent;
import com.lms.www.management.repository.TopicContentRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/content-files")
@RequiredArgsConstructor
public class ContentFileController {

    private final TopicContentRepository topicContentRepository;

    // ===============================
    // 1️⃣ PREVIEW FILE (INLINE)
    // ===============================
    @GetMapping("/preview/{contentId}")
    public ResponseEntity<ByteArrayResource> previewFile(
            @PathVariable Long contentId) throws IOException {

        TopicContent content = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Content not found")
                );

        if (content.getFileUrl() == null) {
            throw new ResourceNotFoundException("File not uploaded yet");
        }

        Path filePath = Paths.get("." + content.getFileUrl());

        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File not found on server");
        }

        byte[] data = Files.readAllBytes(filePath);

        String mimeType = Files.probeContentType(filePath);
        MediaType mediaType = (mimeType != null)
                ? MediaType.parseMediaType(mimeType)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    // ===============================
    // 2️⃣ DOWNLOAD FILE (ATTACHMENT)
    // ===============================
    @GetMapping("/download/{contentId}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable Long contentId) throws IOException {

        TopicContent content = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Content not found")
                );

        if (content.getFileUrl() == null) {
            throw new ResourceNotFoundException("File not uploaded yet");
        }

        // 🔥 Check offline download permission
        Boolean allowOffline =
                topicContentRepository.findAllowOfflineFlag(contentId);

        if (Boolean.FALSE.equals(allowOffline)) {
            throw new UnauthorizedAccessException(
                    "Offline download not allowed for this course"
            );
        }

        Path filePath = Paths.get("." + content.getFileUrl());

        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("File not found on server");
        }

        byte[] data = Files.readAllBytes(filePath);

        String mimeType = Files.probeContentType(filePath);
        MediaType mediaType = (mimeType != null)
                ? MediaType.parseMediaType(mimeType)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filePath.getFileName() + "\""
                )
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}