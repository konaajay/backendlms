package com.lms.www.management.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.SessionContent;
import com.lms.www.management.service.SessionContentService;
import com.lms.www.management.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/session-contents")
@RequiredArgsConstructor
public class SessionContentController {

    private final SessionContentService sessionContentService;

    // ================= CREATE METADATA =================
    @PostMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyAuthority('SESSION_CONTENT_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<SessionContent> createSessionContent(
            @PathVariable Long sessionId,
            @RequestBody SessionContent sessionContent) {

        SessionContent created =
                sessionContentService.createSessionContent(sessionId, sessionContent);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ================= GET BY ID =================
    @GetMapping("/{sessionContentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SessionContent> getSessionContentById(
            @PathVariable Long sessionContentId) {

        return ResponseEntity.ok(
                sessionContentService.getSessionContentById(sessionContentId)
        );
    }

    // ================= GET BY SESSION =================
    @GetMapping("/session/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionContent>> getContentsBySession(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                sessionContentService.getContentsBySessionId(sessionId)
        );
    }

    // ================= UPLOAD FILE =================
    @PutMapping(
    	    value = "/{sessionContentId}/upload",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    	@PreAuthorize("hasAnyAuthority('SESSION_CONTENT_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    	public ResponseEntity<SessionContent> uploadSessionContentFile(
    	        @PathVariable Long sessionContentId,
    	        @RequestParam("file") MultipartFile file) throws IOException {

    	    SessionContent content =
    	            sessionContentService.getSessionContentById(sessionContentId);

    	    // 1️⃣ Save file
    	    String fileUrl = FileUploadUtil.saveSessionContentFile(file);
    	    content.setFileUrl(fileUrl);
    	    
    	    System.out.println("Content Type: " + content.getContentType());

    	    // 2️⃣ If VIDEO → auto extract duration
    	    if ("VIDEO".equalsIgnoreCase(content.getContentType())) {

    	        String absolutePath = Paths.get("")
    	                .toAbsolutePath()
    	                .resolve(fileUrl.substring(1))
    	                .toString();

    	        // 👇 ADD THESE TWO LINES HERE
    	        System.out.println("File URL: " + fileUrl);
    	        System.out.println("Absolute Path: " + absolutePath);

    	        int duration = com.lms.www.management.util.VideoMetadataUtil
    	                .getDurationInSeconds(absolutePath);

    	        content.setTotalDuration(duration);
    	    }

    	    return ResponseEntity.ok(
    	            sessionContentService.updateSessionContent(sessionContentId, content)
    	    );
    	}
    // ================= UPDATE METADATA =================
    @PutMapping("/{sessionContentId}")
    @PreAuthorize("hasAnyAuthority('SESSION_CONTENT_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<SessionContent> updateSessionContent(
            @PathVariable Long sessionContentId,
            @RequestBody SessionContent updatedContent) {

        return ResponseEntity.ok(
                sessionContentService.updateSessionContent(
                        sessionContentId, updatedContent)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{sessionContentId}")
    @PreAuthorize("hasAnyAuthority('SESSION_CONTENT_DELETE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteSessionContent(
            @PathVariable Long sessionContentId) {

        sessionContentService.deleteSessionContent(sessionContentId);
        return ResponseEntity.noContent().build();
    }

    // ================= PREVIEW =================
    @GetMapping("/{sessionContentId}/preview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> previewSessionContent(
            @PathVariable Long sessionContentId) throws IOException {

        SessionContent content =
                sessionContentService.getSessionContentById(sessionContentId);

        if (content.getFileUrl() == null) {
            throw new ResourceNotFoundException("File not uploaded yet");
        }

        Path filePath = Paths.get(content.getFileUrl().substring(1));
        java.net.URI fileUri = filePath.toUri();
        if (fileUri == null) {
            throw new IOException("Could not generate URI for file path");
        }
        Resource resource = new UrlResource(fileUri);

        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found");
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    // ================= DOWNLOAD =================
    @GetMapping("/{sessionContentId}/download")
    @PreAuthorize("hasAuthority('SESSION_CONTENT_DOWNLOAD')")
    public ResponseEntity<Resource> downloadSessionContent(
            @PathVariable Long sessionContentId) throws IOException {

        SessionContent content =
                sessionContentService.getSessionContentById(sessionContentId);

        if (content.getFileUrl() == null) {
            throw new ResourceNotFoundException("File not uploaded yet");
        }

        Path filePath = Paths.get(content.getFileUrl().substring(1));
        java.net.URI fileUri = filePath.toUri();
        if (fileUri == null) {
            throw new IOException("Could not generate URI for file path");
        }
        Resource resource = new UrlResource(fileUri);

        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found");
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }
}