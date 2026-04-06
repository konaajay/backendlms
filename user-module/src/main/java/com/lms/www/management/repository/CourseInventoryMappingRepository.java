package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.CourseInventoryMapping;

public interface CourseInventoryMappingRepository extends JpaRepository<CourseInventoryMapping, Long> {

    List<CourseInventoryMapping> findByCourseId(Long courseId);

}