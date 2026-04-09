package com.lms.www.campus.repository.Library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.campus.Library.BookCategory;

@org.springframework.stereotype.Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

	List<BookCategory> findByIsDeletedFalse();
}