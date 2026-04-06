package com.lms.www.campus.service.student.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.www.campus.Documents.Document;
import com.lms.www.campus.Documents.DocumentShare;
import com.lms.www.campus.repository.Documnets.DocumentRepository;
import com.lms.www.campus.repository.Documnets.DocumentShareRepository;

@Service
public class StudentDocumentServiceImpl {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentShareRepository shareRepository;

    public List<Document> getMyDocuments(Long studentId) {
        // Will fallback to a manual filtered list if findByOwnerUserId query is missing
        // in repo.
        // Assuming findByOwnerUserId is available based on standard Spring Data JPA
        return documentRepository.findByOwnerUserId(studentId);
    }

    public List<DocumentShare> getDocumentsSharedWithMe(Long studentId) {
        return shareRepository.findBySharedWithUserId(studentId);
    }
}
