package com.lms.www.campus.repository.Documnets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Documents.DocumentCategory;

@Repository
public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

	List<DocumentCategory> findByIsDeletedFalse();
}
