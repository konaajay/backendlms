package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.CourseInventoryMapping;
import com.lms.www.management.service.CourseInventoryMappingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/course-inventory")
@RequiredArgsConstructor
public class CourseInventoryMappingController {

    private final CourseInventoryMappingService mappingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('COURSE_INVENTORY_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<CourseInventoryMapping> createMapping(
            @RequestBody CourseInventoryMapping mapping) {

        return ResponseEntity.ok(mappingService.createMapping(mapping));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('COURSE_INVENTORY_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<CourseInventoryMapping>> getAllMappings() {

        return ResponseEntity.ok(mappingService.getAllMappings());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('COURSE_INVENTORY_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<CourseInventoryMapping>> getMappingsByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(mappingService.getMappingsByCourse(courseId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('COURSE_INVENTORY_DELETE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteMapping(@PathVariable Long id) {

        mappingService.deleteMapping(id);
        return ResponseEntity.ok("Mapping deleted successfully");
    }
}