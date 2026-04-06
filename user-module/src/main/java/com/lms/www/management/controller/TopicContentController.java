package com.lms.www.management.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.TopicContent;
import com.lms.www.management.service.TopicContentService;
import com.lms.www.management.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topic-contents")
@RequiredArgsConstructor
public class TopicContentController {

    private final TopicContentService topicContentService;

    // ===============================
    // 1️⃣ CREATE SINGLE CONTENT
    // ===============================
    @PostMapping("/topic/{topicId}")
    @PreAuthorize("hasAuthority('CONTENT_ADD')")
    public ResponseEntity<TopicContent> createContent(
            @PathVariable Long topicId,
            @RequestBody TopicContent content) {

        TopicContent created =
                topicContentService.createContent(topicId, content);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ===============================
    // 2️⃣ CREATE CONTENT BULK
    // ===============================
    @PostMapping("/topic/{topicId}/bulk")
    @PreAuthorize("hasAuthority('CONTENT_ADD')")
    public ResponseEntity<List<TopicContent>> createContentBulk(
            @PathVariable Long topicId,
            @RequestBody List<TopicContent> contents) {

        List<TopicContent> created =
                topicContentService.createContentBulk(topicId, contents);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ===============================
    // 3️⃣ UPDATE CONTENT METADATA
    // ===============================
    @PutMapping("/{contentId}")
    @PreAuthorize("hasAuthority('CONTENT_UPDATE')")
    public ResponseEntity<TopicContent> updateContentMeta(
            @PathVariable Long contentId,
            @RequestBody TopicContent content) {

        TopicContent updated =
                topicContentService.updateContent(contentId, content);

        return ResponseEntity.ok(updated);
    }

    // ===============================
    // 4️⃣ UPLOAD FILES
    // ===============================
    @PutMapping(value = "/upload-files", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('CONTENT_UPDATE')")
    public ResponseEntity<String> uploadContentFiles(
            @RequestParam("contentIds") List<Long> contentIds,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {

        if (contentIds.size() != files.size()) {
            return ResponseEntity.badRequest()
                    .body("Files count must match contentIds count");
        }

        for (int i = 0; i < contentIds.size(); i++) {

            String fileUrl =
                    FileUploadUtil.saveTopicContentFile(files.get(i));

            TopicContent update = new TopicContent();
            update.setFileUrl(fileUrl);

            topicContentService.updateContent(contentIds.get(i), update);
        }

        return ResponseEntity.ok("Files uploaded successfully");
    }

    // ===============================
    // 5️⃣ GET ALL CONTENTS
    // ===============================
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TopicContent>> getAllContents() {
        return ResponseEntity.ok(
                topicContentService.getAllContents()
        );
    }

    // ===============================
    // 6️⃣ GET CONTENT BY ID
    // ===============================
    @GetMapping("/{contentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicContent> getContentById(
            @PathVariable Long contentId) {

        return ResponseEntity.ok(
                topicContentService.getContentById(contentId)
        );
    }

    // ===============================
    // 7️⃣ GET CONTENTS BY TOPIC
    // ===============================
    @GetMapping("/topic/{topicId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TopicContent>> getContentsByTopic(
            @PathVariable Long topicId) {

        return ResponseEntity.ok(
                topicContentService.getContentsByTopicId(topicId)
        );
    }

    // ===============================
    // 8️⃣ DELETE CONTENT
    // ===============================
    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasAuthority('CONTENT_DELETE')")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId) {

        topicContentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}