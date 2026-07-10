package com.lms.www.management.repository;

import com.lms.www.management.model.CourseBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {
    List<CourseBookmark> findByUserId(Long userId);
    Optional<CourseBookmark> findByCourseIdAndUserId(Long courseId, Long userId);
    void deleteByCourseIdAndUserId(Long courseId, Long userId);
}
