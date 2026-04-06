package com.lms.www.campus.repository.Documnets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Documents.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByIsDeletedFalse();
    List<Document> findByOwnerUserId(Long ownerUserId);
}

