package com.lms.www.campus.repository.Library;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.campus.Library.LibraryMember;

public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Long> {

	List<LibraryMember> findByIsDeletedFalse();
}
