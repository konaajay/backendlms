package com.lms.www.campus.repository.Library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.lms.www.campus.Library.LibraryFine;

public interface LibraryFineRepository extends JpaRepository<LibraryFine, Long> {

	@EntityGraph(attributePaths = { "issueRecord", "issueRecord.book", "issueRecord.book.category", "issueRecord.book.barcodes" })
	@org.springframework.lang.NonNull
	List<LibraryFine> findAll();

	@EntityGraph(attributePaths = { "issueRecord", "issueRecord.book", "issueRecord.book.category", "issueRecord.book.barcodes" })
	List<LibraryFine> findByIsDeletedFalse();

	@EntityGraph(attributePaths = { "issueRecord", "issueRecord.book", "issueRecord.book.category", "issueRecord.book.barcodes" })
	List<LibraryFine> findByUserId(Long userId);
}