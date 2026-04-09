package com.lms.www.campus.repository.Library;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import com.lms.www.campus.Library.Books;

@org.springframework.stereotype.Repository
public interface BooksRepository extends JpaRepository<Books, Long> {

	@EntityGraph(attributePaths = { "barcodes", "category" })
	List<Books> findByIsDeletedFalse();

	@EntityGraph(attributePaths = { "barcodes", "category" })
    Page<Books> findByIsDeletedFalse(Pageable pageable);

    Optional<Books> findByIsbnAndIsDeletedFalse(String isbn);

    List<Books> findByCategory_IdAndIsDeletedFalse(Long categoryId);
    
    long countByIsDeletedFalse();

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Books b SET b.availableCopies = :copies, b.status = :status WHERE b.id = :id")
    void updateBookStatusAndCopies(Long id, Integer copies, Books.Status status);
}
