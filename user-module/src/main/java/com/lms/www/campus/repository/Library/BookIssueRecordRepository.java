package com.lms.www.campus.repository.Library;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.campus.Library.BookIssueRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@org.springframework.stereotype.Repository
public interface BookIssueRecordRepository extends JpaRepository<BookIssueRecord, Long> {

	long countByUserIdAndStatus(Long userId, BookIssueRecord.Status status);

	long countByBook_IdAndStatus(Long bookId, BookIssueRecord.Status status);

	long countByBook_IdAndStatusIn(Long bookId, List<BookIssueRecord.Status> statuses);

	long countByStatus(BookIssueRecord.Status status);

	long countByStatusIn(List<BookIssueRecord.Status> statuses);

	@Query("SELECT COUNT(DISTINCT b.userId) FROM BookIssueRecord b")
	long countActiveMembers();

	List<BookIssueRecord> findTop10ByOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = { "book", "book.category", "book.barcodes" })
	List<BookIssueRecord> findAll();

	@EntityGraph(attributePaths = { "book", "book.category" })
	Page<BookIssueRecord> findByUserId(Long userId, Pageable pageable);

	@EntityGraph(attributePaths = { "book", "book.category" })
	List<BookIssueRecord> findByUserIdAndStatus(Long userId, BookIssueRecord.Status status);

	@EntityGraph(attributePaths = { "book", "book.category" })
	List<BookIssueRecord> findByUserIdAndStatusIn(Long userId, List<BookIssueRecord.Status> statuses);
}
