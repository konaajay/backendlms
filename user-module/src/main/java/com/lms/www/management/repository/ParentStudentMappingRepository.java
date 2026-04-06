package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ParentStudentMapping;

@Repository
public interface ParentStudentMappingRepository extends JpaRepository<ParentStudentMapping, Long> {

    @Query("SELECT p.studentId FROM ParentStudentMapping p WHERE p.parentUserId = :parentUserId")
    List<Long> findStudentIdsByParentUserId(@Param("parentUserId") Long parentUserId);

    // Check if mapping exists to authorize parent access
    boolean existsByParentUserIdAndStudentId(Long parentUserId, Long studentId);
}