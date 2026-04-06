package com.lms.www.campus.repository.Library;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lms.www.campus.Library.Books;

public interface BooksRepository extends JpaRepository<Books, Long> {

	@org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "barcodes", "category" })
	List<Books> findByIsDeletedFalse();

    Optional<Books> findByIsbnAndIsDeletedFalse(String isbn);

    List<Books> findByCategory_IdAndIsDeletedFalse(Long categoryId);
    
    long countByIsDeletedFalse();

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Books b SET b.availableCopies = :copies, b.status = :status WHERE b.id = :id")
    void updateBookStatusAndCopies(Long id, Integer copies, Books.Status status);
}
