package com.lms.www.campus.repository.Library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.lms.www.campus.Library.BookReservation;

public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {

	@EntityGraph(attributePaths = { "book", "book.category", "book.barcodes" })
	List<BookReservation> findByIsDeletedFalse();

	@EntityGraph(attributePaths = { "book", "book.category", "book.barcodes" })
	List<BookReservation> findByUserIdAndIsDeletedFalse(Long userId);

	List<BookReservation> findByStatusAndIsDeletedFalse(BookReservation.Status status);

	List<BookReservation> findByReserveUntilBeforeAndStatusAndIsDeletedFalse(java.time.LocalDate date,
			BookReservation.Status status);

	List<BookReservation> findByAdminHoldUntilBeforeAndStatusAndIsDeletedFalse(java.time.LocalDate date,
			BookReservation.Status status);

	// Explicit underscore traversal: book.id — used by LibraryServiceImpl
	List<BookReservation> findByBook_IdAndUserIdAndIsDeletedFalse(Long bookId, Long userId);

	List<BookReservation> findByBook_IdAndIsDeletedFalse(Long bookId);

	long countByBook_IdAndStatusAndIsDeletedFalse(Long bookId, BookReservation.Status status);

}
