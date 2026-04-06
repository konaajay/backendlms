package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByShareCode(String shareCode);
}