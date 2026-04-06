package com.lms.www.campus.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lms.www.dto.StudentApiContract.*;
import com.lms.www.security.UserContext;
import com.lms.www.campus.Documents.*;
import com.lms.www.campus.service.student.impl.StudentDocumentServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/documents")
public class StudentDocumentController {

    private static final Logger log = LoggerFactory.getLogger(StudentDocumentController.class);

    @Autowired
    private StudentDocumentServiceImpl documentService;

    @Autowired
    private UserContext userContext;

    @GetMapping("/my-documents")
    public ApiResponse<List<DocumentDto>> getMyDocuments() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching documents for studentId: {}", studentId);

        List<Document> docs = documentService.getMyDocuments(studentId);

        List<DocumentDto> dtos = docs.stream().map(d -> DocumentDto.builder()
                .documentId(d.getDocumentId())
                .documentName(d.getTitle())
                .documentType("OWNED")
                .status(d.getStatus() != null ? d.getStatus().toString() : "ACTIVE")
                .uploadedAt(d.getCreatedAt())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Documents fetched successfully");
    }

    @GetMapping("/shared-with-me")
    public ApiResponse<List<DocumentDto>> getSharedDocuments() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching shared documents for studentId: {}", studentId);

        List<DocumentShare> shares = documentService.getDocumentsSharedWithMe(studentId);

        List<DocumentDto> dtos = shares.stream().map(s -> DocumentDto.builder()
                .documentId(s.getShareId())
                .documentName(
                        s.getDocument() != null ? s.getDocument().getTitle() : "Shared Document " + s.getShareId())
                .documentType("SHARED")
                .status("AVAILABLE")
                .uploadedAt(s.getCreatedAt())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Shared documents fetched successfully");
    }
}
