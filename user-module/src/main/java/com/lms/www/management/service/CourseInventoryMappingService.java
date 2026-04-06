package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.CourseInventoryMapping;

public interface CourseInventoryMappingService {

    CourseInventoryMapping createMapping(CourseInventoryMapping mapping);

    List<CourseInventoryMapping> getMappingsByCourse(Long courseId);

    List<CourseInventoryMapping> getAllMappings();

    void deleteMapping(Long id);

}